package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;

import javax.annotation.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(Dist.CLIENT)
public class MultiblockVisualizationHandler {

	public static boolean hasMultiblock;
	public static Bookmark bookmark;

	private static IMultiblock multiblock;
	private static ITextComponent name;
	private static BlockPos pos;
	private static boolean isAnchored;
	private static Rotation facingRotation;
	private static Function<BlockPos, BlockPos> offsetApplier;
	private static int blocks, blocksDone, airFilled;
	private static int timeComplete;
	private static BlockState lookingState;
	private static BlockPos lookingPos;
	private static IRenderTypeBuffer.Impl buffers = null;

	public static void setMultiblock(IMultiblock multiblock, ITextComponent name, Bookmark bookmark, boolean flip) {
		setMultiblock(multiblock, name, bookmark, flip, pos -> pos);
	}

	public static void setMultiblock(IMultiblock multiblock, ITextComponent name, Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
		if (flip && hasMultiblock) {
			hasMultiblock = false;
		} else {
			MultiblockVisualizationHandler.multiblock = multiblock;
			MultiblockVisualizationHandler.name = name;
			MultiblockVisualizationHandler.bookmark = bookmark;
			MultiblockVisualizationHandler.offsetApplier = offsetApplier;
			pos = null;
			hasMultiblock = multiblock != null;
			isAnchored = false;
		}
	}

	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL && hasMultiblock) {
			MatrixStack ms = event.getMatrixStack();
			Minecraft mc = Minecraft.getInstance();
			int waitTime = 40;
			int fadeOutSpeed = 4;
			int fullAnimTime = waitTime + 10;
			float animTime = timeComplete + (timeComplete == 0 ? 0 : event.getPartialTicks());

			if (animTime > fullAnimTime) {
				hasMultiblock = false;
				return;
			}

			ms.push();
			ms.translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

			int x = event.getWindow().getScaledWidth() / 2;
			int y = 12;

			mc.fontRenderer.func_238407_a_(ms, name, x - mc.fontRenderer.func_238414_a_(name) / 2, y, 0xFFFFFF);

			int width = 180;
			int height = 9;
			int left = x - width / 2;
			int top = y + 10;

			if (timeComplete > 0) {
				String s = I18n.format("patchouli.gui.lexicon.structure_complete");
				ms.push();
				ms.translate(0, Math.min(height + 5, animTime), 0);
				mc.fontRenderer.drawStringWithShadow(ms, s, x - mc.fontRenderer.getStringWidth(s) / 2, top + height - 10, 0x00FF00);
				ms.pop();
			}

			AbstractGui.fill(ms, left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
			drawGradientRect(ms, left, top, left + width, top + height, 0xFF666666, 0xFF555555);

			float fract = (float) blocksDone / Math.max(1, blocks);
			int progressWidth = (int) ((float) width * fract);
			int color = MathHelper.hsvToRGB(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
			int color2 = new Color(color).darker().getRGB();
			drawGradientRect(ms, left, top, left + progressWidth, top + height, color, color2);

			if (!isAnchored) {
				String s = I18n.format("patchouli.gui.lexicon.not_anchored");
				mc.fontRenderer.drawStringWithShadow(ms, s, x - mc.fontRenderer.getStringWidth(s) / 2, top + height + 8, 0xFFFFFF);
			} else {
				if (lookingState != null) {
					// try-catch around here because the state isn't necessarily present in the world in this instance,
					// which isn't really expected behavior for getPickBlock
					try {
						Block block = lookingState.getBlock();
						ItemStack stack = block.getPickBlock(lookingState, mc.objectMouseOver, mc.world, lookingPos, mc.player);

						if (!stack.isEmpty()) {
							mc.fontRenderer.func_238407_a_(ms, stack.getDisplayName(), left + 20, top + height + 8, 0xFFFFFF);
							RenderHelper.renderItemStackInGui(ms, stack, left, top + height + 2);
						}
					} catch (Exception ignored) {}
				}

				if (timeComplete == 0) {
					color = 0xFFFFFF;
					int posx = left + width;
					int posy = top + height + 2;
					int mult = 1;
					String progress = blocksDone + "/" + blocks;

					if (blocksDone == blocks && airFilled > 0) {
						progress = I18n.format("patchouli.gui.lexicon.needs_air");
						color = 0xDA4E3F;
						mult *= 2;
						posx -= width / 2;
						posy += 2;
					}

					mc.fontRenderer.drawStringWithShadow(ms, progress, posx - mc.fontRenderer.getStringWidth(progress) / mult, posy, color);
				}
			}

			ms.pop();
		}
	}

	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent event) {
		if (hasMultiblock && multiblock != null) {
			renderMultiblock(Minecraft.getInstance().world, event.getMatrixStack());
		}
	}

	public static void anchorTo(BlockPos target, Rotation rot) {
		pos = target;
		facingRotation = rot;
		isAnchored = true;
	}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if (hasMultiblock && !isAnchored && event.getPlayer() == Minecraft.getInstance().player) {
			anchorTo(event.getPos(), getRotation(event.getPlayer()));
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().world == null) {
			hasMultiblock = false;
		} else if (isAnchored && blocks == blocksDone && airFilled == 0) {
			timeComplete++;
			if (timeComplete == 14) {
				Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
			}
		} else {
			timeComplete = 0;
		}
	}

	public static void renderMultiblock(World world, MatrixStack ms) {
		Minecraft mc = Minecraft.getInstance();
		if (!isAnchored) {
			facingRotation = getRotation(mc.player);
			if (mc.objectMouseOver instanceof BlockRayTraceResult) {
				pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
			}
		} else if (pos.distanceSq(mc.player.getPositionVec(), false) > 64 * 64) {
			return;
		}

		if (pos == null) {
			return;
		}
		if (multiblock.isSymmetrical()) {
			facingRotation = Rotation.NONE;
		}

		EntityRendererManager erd = mc.getRenderManager();
		double renderPosX = erd.info.getProjectedView().getX();
		double renderPosY = erd.info.getProjectedView().getY();
		double renderPosZ = erd.info.getProjectedView().getZ();
		ms.translate(-renderPosX, -renderPosY, -renderPosZ);

		if (buffers == null) {
			buffers = initBuffers(mc.getRenderTypeBuffers().getBufferSource());
		}

		BlockPos checkPos = null;
		if (mc.objectMouseOver instanceof BlockRayTraceResult) {
			BlockRayTraceResult blockRes = (BlockRayTraceResult) mc.objectMouseOver;
			checkPos = blockRes.getPos().offset(blockRes.getFace());
		}

		blocks = blocksDone = airFilled = 0;
		lookingState = null;
		lookingPos = checkPos;

		Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = multiblock.simulate(world, getStartPos(), getFacingRotation(), true);
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			float alpha = 0.3F;
			if (r.getWorldPosition().equals(checkPos)) {
				lookingState = r.getStateMatcher().getDisplayedState((int) ClientTicker.ticksInGame);
				alpha = 0.6F + (float) (Math.sin(ClientTicker.total * 0.3F) + 1F) * 0.1F;
			}

			if (r.getStateMatcher() != StateMatcher.ANY) {
				boolean air = r.getStateMatcher() == StateMatcher.AIR;
				if (!air) {
					blocks++;
				}

				if (!r.test(world, facingRotation)) {
					BlockState renderState = r.getStateMatcher().getDisplayedState((int) ClientTicker.ticksInGame).rotate(facingRotation);
					renderBlock(world, renderState, r.getWorldPosition(), alpha, ms);

					if (air) {
						airFilled++;
					}
				} else if (!air) {
					blocksDone++;
				}
			}
		}

		buffers.finish();

		if (!isAnchored) {
			blocks = blocksDone = 0;
		}
	}

	public static void renderBlock(World world, BlockState state, BlockPos pos, float alpha, MatrixStack ms) {
		if (pos != null) {
			ms.push();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());

			if (state.getBlock() == Blocks.AIR) {
				float scale = 0.3F;
				float off = (1F - scale) / 2;
				ms.translate(off, off, -off);
				ms.scale(scale, scale, scale);

				state = Blocks.RED_CONCRETE.getDefaultState();
			}

			Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(state, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);

			ms.pop();
		}
	}

	public static IMultiblock getMultiblock() {
		return multiblock;
	}

	public static boolean isAnchored() {
		return isAnchored;
	}

	public static Rotation getFacingRotation() {
		return multiblock.isSymmetrical() ? Rotation.NONE : facingRotation;
	}

	public static BlockPos getStartPos() {
		return offsetApplier.apply(pos);
	}

	private static void drawGradientRect(MatrixStack ms, int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		Matrix4f mat = ms.getLast().getMatrix();
		bufferbuilder.pos(mat, right, top, 0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos(mat, left, top, 0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos(mat, left, bottom, 0).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos(mat, right, bottom, 0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
	 */
	private static Rotation getRotation(Entity entity) {
		return RotationUtil.rotationFromFacing(entity.getHorizontalFacing());
	}

	private static IRenderTypeBuffer.Impl initBuffers(IRenderTypeBuffer.Impl original) {
		BufferBuilder fallback = ObfuscationReflectionHelper.getPrivateValue(IRenderTypeBuffer.Impl.class, original, "field_228457_a_");
		Map<RenderType, BufferBuilder> layerBuffers = ObfuscationReflectionHelper.getPrivateValue(IRenderTypeBuffer.Impl.class, original, "field_228458_b_");
		Map<RenderType, BufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
		for (Map.Entry<RenderType, BufferBuilder> e : layerBuffers.entrySet()) {
			remapped.put(GhostRenderType.remap(e.getKey()), e.getValue());
		}
		return new GhostBuffers(fallback, remapped);
	}

	private static class GhostBuffers extends IRenderTypeBuffer.Impl {
		protected GhostBuffers(BufferBuilder fallback, Map<RenderType, BufferBuilder> layerBuffers) {
			super(fallback, layerBuffers);
		}

		@Override
		public IVertexBuilder getBuffer(RenderType type) {
			return super.getBuffer(GhostRenderType.remap(type));
		}
	}

	private static class GhostRenderType extends RenderType {
		private static Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

		private GhostRenderType(RenderType original) {
			super(String.format("%s_%s_ghost", original.toString(), Patchouli.MOD_ID), original.getVertexFormat(), original.getDrawMode(), original.getBufferSize(), original.isUseDelegate(), true, () -> {
				original.setupRenderState();

				// Alter GL state
				RenderSystem.disableDepthTest();
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
				RenderSystem.blendColor(1, 1, 1, 0.4F);
			}, () -> {
				RenderSystem.blendColor(1, 1, 1, 1);
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableBlend();
				RenderSystem.enableDepthTest();

				original.clearRenderState();
			});
		}

		@Override
		public boolean equals(@Nullable Object other) {
			return this == other;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

		public static RenderType remap(RenderType in) {
			if (in instanceof GhostRenderType) {
				return in;
			} else {
				return remappedTypes.computeIfAbsent(in, GhostRenderType::new);
			}
		}
	}

}
