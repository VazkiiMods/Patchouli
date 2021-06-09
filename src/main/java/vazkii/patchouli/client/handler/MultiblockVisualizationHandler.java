package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;
import vazkii.patchouli.mixin.client.AccessorVertexConsumerProviderImmediate;

import javax.annotation.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class MultiblockVisualizationHandler {

	public static boolean hasMultiblock;
	public static Bookmark bookmark;

	private static IMultiblock multiblock;
	private static Text name;
	private static BlockPos pos;
	private static boolean isAnchored;
	private static BlockRotation facingRotation;
	private static Function<BlockPos, BlockPos> offsetApplier;
	private static int blocks, blocksDone, airFilled;
	private static int timeComplete;
	private static BlockState lookingState;
	private static BlockPos lookingPos;
	private static VertexConsumerProvider.Immediate buffers = null;

	public static void setMultiblock(IMultiblock multiblock, Text name, Bookmark bookmark, boolean flip) {
		setMultiblock(multiblock, name, bookmark, flip, pos -> pos);
	}

	public static void setMultiblock(IMultiblock multiblock, Text name, Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
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

	public static void onRenderHUD(MatrixStack ms, float partialTicks) {
		if (hasMultiblock) {
			int waitTime = 40;
			int fadeOutSpeed = 4;
			int fullAnimTime = waitTime + 10;
			float animTime = timeComplete + (timeComplete == 0 ? 0 : partialTicks);

			if (animTime > fullAnimTime) {
				hasMultiblock = false;
				return;
			}

			ms.push();
			ms.translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

			MinecraftClient mc = MinecraftClient.getInstance();
			int x = mc.getWindow().getScaledWidth() / 2;
			int y = 12;

			mc.textRenderer.drawWithShadow(ms, name, x - mc.textRenderer.getWidth(name) / 2, y, 0xFFFFFF);

			int width = 180;
			int height = 9;
			int left = x - width / 2;
			int top = y + 10;

			if (timeComplete > 0) {
				String s = I18n.translate("patchouli.gui.lexicon.structure_complete");
				ms.push();
				ms.translate(0, Math.min(height + 5, animTime), 0);
				mc.textRenderer.drawWithShadow(ms, s, x - mc.textRenderer.getWidth(s) / 2, top + height - 10, 0x00FF00);
				ms.pop();
			}

			DrawableHelper.fill(ms, left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
			drawGradientRect(ms, left, top, left + width, top + height, 0xFF666666, 0xFF555555);

			float fract = (float) blocksDone / Math.max(1, blocks);
			int progressWidth = (int) ((float) width * fract);
			int color = MathHelper.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
			int color2 = new Color(color).darker().getRGB();
			drawGradientRect(ms, left, top, left + progressWidth, top + height, color, color2);

			if (!isAnchored) {
				String s = I18n.translate("patchouli.gui.lexicon.not_anchored");
				mc.textRenderer.drawWithShadow(ms, s, x - mc.textRenderer.getWidth(s) / 2, top + height + 8, 0xFFFFFF);
			} else {
				if (lookingState != null) {
					// try-catch around here because the state isn't necessarily present in the world in this instance,
					// which isn't really expected behavior for getPickBlock
					try {
						Block block = lookingState.getBlock();
						ItemStack stack = block.getPickStack(mc.world, lookingPos, lookingState);

						if (!stack.isEmpty()) {
							mc.textRenderer.drawWithShadow(ms, stack.getName(), left + 20, top + height + 8, 0xFFFFFF);
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
						progress = I18n.translate("patchouli.gui.lexicon.needs_air");
						color = 0xDA4E3F;
						mult *= 2;
						posx -= width / 2;
						posy += 2;
					}

					mc.textRenderer.drawWithShadow(ms, progress, posx - mc.textRenderer.getWidth(progress) / mult, posy, color);
				}
			}

			ms.pop();
		}
	}

	public static void onWorldRenderLast(MatrixStack ms) {
		if (hasMultiblock && multiblock != null) {
			renderMultiblock(MinecraftClient.getInstance().world, ms);
		}
	}

	public static void anchorTo(BlockPos target, BlockRotation rot) {
		pos = target;
		facingRotation = rot;
		isAnchored = true;
	}

	private static ActionResult onPlayerInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		if (hasMultiblock && !isAnchored && player == MinecraftClient.getInstance().player) {
			anchorTo(hit.getBlockPos(), getRotation(player));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	public static void onClientTick(MinecraftClient mc) {
		if (MinecraftClient.getInstance().world == null) {
			hasMultiblock = false;
		} else if (isAnchored && blocks == blocksDone && airFilled == 0) {
			timeComplete++;
			if (timeComplete == 14) {
				MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
			}
		} else {
			timeComplete = 0;
		}
	}

	public static void init() {
		UseBlockCallback.EVENT.register(MultiblockVisualizationHandler::onPlayerInteract);
		ClientTickEvents.END_CLIENT_TICK.register(MultiblockVisualizationHandler::onClientTick);
		HudRenderCallback.EVENT.register(MultiblockVisualizationHandler::onRenderHUD);
	}

	public static void renderMultiblock(World world, MatrixStack ms) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (!isAnchored) {
			facingRotation = getRotation(mc.player);
			if (mc.crosshairTarget instanceof BlockHitResult) {
				pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
			}
		} else if (pos.getSquaredDistance(mc.player.getPos(), false) > 64 * 64) {
			return;
		}

		if (pos == null) {
			return;
		}
		if (multiblock.isSymmetrical()) {
			facingRotation = BlockRotation.NONE;
		}

		EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
		double renderPosX = erd.camera.getPos().getX();
		double renderPosY = erd.camera.getPos().getY();
		double renderPosZ = erd.camera.getPos().getZ();
		ms.translate(-renderPosX, -renderPosY, -renderPosZ);

		if (buffers == null) {
			buffers = initBuffers(mc.getBufferBuilders().getEntityVertexConsumers());
		}

		BlockPos checkPos = null;
		if (mc.crosshairTarget instanceof BlockHitResult) {
			BlockHitResult blockRes = (BlockHitResult) mc.crosshairTarget;
			checkPos = blockRes.getBlockPos().offset(blockRes.getSide());
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

		buffers.draw();

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

			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, ms, buffers, 0xF000F0, OverlayTexture.DEFAULT_UV);

			ms.pop();
		}
	}

	public static IMultiblock getMultiblock() {
		return multiblock;
	}

	public static boolean isAnchored() {
		return isAnchored;
	}

	public static BlockRotation getFacingRotation() {
		return multiblock.isSymmetrical() ? BlockRotation.NONE : facingRotation;
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
		//RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		//RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Matrix4f mat = ms.peek().getModel();
		bufferbuilder.vertex(mat, right, top, 0).color(f1, f2, f3, f).next();
		bufferbuilder.vertex(mat, left, top, 0).color(f1, f2, f3, f).next();
		bufferbuilder.vertex(mat, left, bottom, 0).color(f5, f6, f7, f4).next();
		bufferbuilder.vertex(mat, right, bottom, 0).color(f5, f6, f7, f4).next();
		tessellator.draw();
		//RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		//RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
	 */
	private static BlockRotation getRotation(Entity entity) {
		return RotationUtil.rotationFromFacing(entity.getHorizontalFacing());
	}

	private static VertexConsumerProvider.Immediate initBuffers(VertexConsumerProvider.Immediate original) {
		BufferBuilder fallback = ((AccessorVertexConsumerProviderImmediate) original).getFallbackBuffer();
		Map<RenderLayer, BufferBuilder> layerBuffers = ((AccessorVertexConsumerProviderImmediate) original).getLayerBuffers();
		Map<RenderLayer, BufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
		for (Map.Entry<RenderLayer, BufferBuilder> e : layerBuffers.entrySet()) {
			remapped.put(GhostRenderLayer.remap(e.getKey()), e.getValue());
		}
		return new GhostBuffers(fallback, remapped);
	}

	private static class GhostBuffers extends VertexConsumerProvider.Immediate {
		protected GhostBuffers(BufferBuilder fallback, Map<RenderLayer, BufferBuilder> layerBuffers) {
			super(fallback, layerBuffers);
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer type) {
			return super.getBuffer(GhostRenderLayer.remap(type));
		}
	}

	private static class GhostRenderLayer extends RenderLayer {
		private static final Map<RenderLayer, RenderLayer> remappedTypes = new IdentityHashMap<>();

		private GhostRenderLayer(RenderLayer original) {
			super(String.format("%s_%s_ghost", original.toString(), Patchouli.MOD_ID), original.getVertexFormat(), original.getDrawMode(), original.getExpectedBufferSize(), original.hasCrumbling(), true, () -> {
				original.startDrawing();

				// Alter GL state
				RenderSystem.disableDepthTest();
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SrcFactor.CONSTANT_ALPHA, GlStateManager.DstFactor.ONE_MINUS_CONSTANT_ALPHA);
				RenderSystem.setShaderColor(1, 1, 1, 0.4F);
			}, () -> {
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableBlend();
				RenderSystem.enableDepthTest();

				original.endDrawing();
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

		public static RenderLayer remap(RenderLayer in) {
			if (in instanceof GhostRenderLayer) {
				return in;
			} else {
				return remappedTypes.computeIfAbsent(in, GhostRenderLayer::new);
			}
		}
	}

}
