package vazkii.patchouli.client.handler;

import java.awt.Color;
import java.util.Collection;
import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;

public class MultiblockVisualizationHandler {

	public static boolean hasMultiblock;
	public static Bookmark bookmark;

	private static IMultiblock multiblock;
	private static String name;
	private static BlockPos pos;
	private static boolean isAnchored;
	private static BlockRotation facingBlockRotation;
	private static Function<BlockPos, BlockPos> offsetApplier;
	private static int blocks, blocksDone, airFilled;
	private static int timeComplete;
	private static BlockState lookingState;
	private static BlockPos lookingPos;

	public static void setMultiblock(IMultiblock multiblock, String name, Bookmark bookmark, boolean flip) {
		setMultiblock(multiblock, name, bookmark, flip, pos->pos);
	}

	public static void setMultiblock(IMultiblock multiblock, String name, Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
		if(flip && hasMultiblock)
			hasMultiblock = false;
		else {
			MultiblockVisualizationHandler.multiblock = multiblock;
			MultiblockVisualizationHandler.name = name;
			MultiblockVisualizationHandler.bookmark = bookmark;
			MultiblockVisualizationHandler.offsetApplier = offsetApplier;
			pos = null;
			hasMultiblock = true;
			isAnchored = false;
		}
	}

	public static void onRenderHUD(float partialTicks) {
		if(hasMultiblock) {
			int waitTime = 40;
			int fadeOutSpeed = 4;
			int fullAnimTime = waitTime + 10;
			float animTime = timeComplete + (timeComplete == 0 ? 0 : partialTicks);

			if(animTime > fullAnimTime) {
				hasMultiblock = false;
				return;
			}

			RenderSystem.pushMatrix();
			RenderSystem.translatef(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

			MinecraftClient mc = MinecraftClient.getInstance();
			int x = mc.getWindow().getScaledWidth() / 2;
			int y = 12;

			mc.textRenderer.drawWithShadow(name, x - mc.textRenderer.getStringWidth(name) / 2, y , 0xFFFFFF);

			int width = 180;
			int height = 9;
			int left = x - width / 2;
			int top = y + 10;

			if(timeComplete > 0) {
				String s = I18n.translate("patchouli.gui.lexicon.structure_complete");
				RenderSystem.pushMatrix();
				RenderSystem.translatef(0, Math.min(height + 5, animTime), 0);
				mc.textRenderer.drawWithShadow(s, x - mc.textRenderer.getStringWidth(s) / 2, top + height - 10, 0x00FF00);
				RenderSystem.popMatrix();
			}

			DrawableHelper.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
			drawGradientRect(left, top, left + width, top + height, 0xFF666666, 0xFF555555);

			float fract = (float) blocksDone / Math.max(1, blocks);
			int progressWidth = (int) ((float) width * fract);
			int color = MathHelper.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
			int color2 = new Color(color).darker().getRGB();
			drawGradientRect(left, top, left + progressWidth, top + height, color, color2);

			if(!isAnchored) {
				String s = I18n.translate("patchouli.gui.lexicon.not_anchored");
				mc.textRenderer.drawWithShadow(s, x - mc.textRenderer.getStringWidth(s) / 2, top + height + 8, 0xFFFFFF);
			} else {
				if(lookingState != null) {
					// try-catch around here because the state isn't necessarily present in the world in this instance,
					// which isn't really expected behavior for getPickBlock
					try {
						Block block = lookingState.getBlock();
						ItemStack stack = block.getPickStack(mc.world, lookingPos, lookingState);

						if (!stack.isEmpty()) {
							mc.textRenderer.drawWithShadow(stack.getName().asFormattedString(), left + 20, top + height + 8, 0xFFFFFF);
							mc.getItemRenderer().renderGuiItem(stack, left, top + height + 2);
						}
					} catch(Exception ignored) {}
				}

				if(timeComplete == 0) {
					color = 0xFFFFFF;
					int posx = left + width;
					int posy = top + height + 2;
					int mult = 1;
					String progress = blocksDone + "/" + blocks;

					if(blocksDone == blocks && airFilled > 0) {
						progress = I18n.translate("patchouli.gui.lexicon.needs_air");
						color = 0xDA4E3F;
						mult *= 2;
						posx -= width / 2;
						posy += 2;
					}

					mc.textRenderer.drawWithShadow(progress, posx - mc.textRenderer.getStringWidth(progress) / mult, posy, color);
				}
			}

			RenderSystem.popMatrix();
		}
	}


	public static void anchorTo(BlockPos target, BlockRotation rot) {
		pos = target;
		facingBlockRotation = rot;
		isAnchored = true;
	}

	public static ActionResult onPlayerInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		if(hasMultiblock && !isAnchored && player == MinecraftClient.getInstance().player) {
			anchorTo(hit.getBlockPos(), getBlockRotation(player));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	public static void init() {
		UseBlockCallback.EVENT.register(MultiblockVisualizationHandler::onPlayerInteract);
		ClientTickCallback.EVENT.register(MultiblockVisualizationHandler::onClientTick);
	}

	private static void onClientTick(MinecraftClient mc) {
		if(mc.world == null)
			hasMultiblock = false;
		else if(isAnchored && blocks == blocksDone && airFilled == 0) {
			timeComplete++;
			if(timeComplete == 14)
				mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
		} else timeComplete = 0;
	}

	/*  todo 1.15/fabric
	public static void onWorldRenderLast(RenderWorldLastEvent event) {
		if(hasMultiblock && multiblock != null)
			renderMultiblock(MinecraftClient.getInstance().world);
	}

	public static void renderMultiblock(World world) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if(!isAnchored) {
			facingBlockRotation = getBlockRotation(mc.player);
			if(mc.objectMouseOver instanceof BlockRayTraceResult)
				pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
		}
		else if(pos.distanceSq(mc.player.getPosition()) > 64 * 64)
			return;

		if(pos == null)
			return;
		if(multiblock.isSymmetrical())
			facingBlockRotation = BlockRotation.NONE;

		EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		
		double renderPosX = ObfuscationReflectionHelper.getPrivateValue(EntityRendererManager.class, manager, "field_78725_b");
		double renderPosY = ObfuscationReflectionHelper.getPrivateValue(EntityRendererManager.class, manager, "field_78726_c");
		double renderPosZ = ObfuscationReflectionHelper.getPrivateValue(EntityRendererManager.class, manager, "field_78723_d");

		RenderSystem.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(RenderSystem.SourceFactor.CONSTANT_ALPHA, RenderSystem.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
		RenderSystem.disableDepthTest();
		RenderSystem.disableLighting();
		RenderSystem.translated(-renderPosX, -renderPosY, -renderPosZ);

		BlockPos checkPos = null;
		if(mc.objectMouseOver instanceof BlockRayTraceResult) {
			BlockRayTraceResult blockRes = (BlockRayTraceResult) mc.objectMouseOver;
			checkPos = blockRes.getPos().offset(blockRes.getFace());
		}

		blocks = blocksDone = airFilled = 0;
		lookingState = null;
		lookingPos = checkPos;

		Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = multiblock.simulate(world, getStartPos(), getFacingBlockRotation(), true);
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			float alpha = 0.3F;
			if(r.getWorldPosition().equals(checkPos)) {
				lookingState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
				alpha = 0.6F + (float) (Math.sin(ClientTicker.total * 0.3F) + 1F) * 0.1F;
			}

			if(r.getStateMatcher() != StateMatcher.ANY) {
				boolean air = r.getStateMatcher() == StateMatcher.AIR;
				if(!air)
					blocks++;

				if(!r.test(world, facingBlockRotation)) {
					BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame).rotate(facingBlockRotation);
					mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
					renderBlock(world, renderState, r.getWorldPosition(), alpha, dispatcher);

					if(air)
						airFilled++;
				} else if(!air)
					blocksDone++;
			}
		}

		if(!isAnchored)
			blocks = blocksDone = 0;

		GL11.glPopAttrib();
		GL14.glBlendColor(1F, 1F, 1F, 1F);
		RenderSystem.enableDepthTest();
		RenderSystem.popMatrix();
	}

	public static void renderBlock(World world, BlockState state, BlockPos pos, float alpha, BlockRendererDispatcher brd) {
		if(pos != null) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef(pos.getX(), pos.getY(), pos.getZ());
			RenderSystem.color4f(1F, 1F, 1F, 1F);
			RenderSystem.rotatef(-90F, 0F, 1F, 0F);
			GL14.glBlendColor(1F, 1F, 1F, alpha);

			try {
				if(state.getBlock() == Blocks.AIR) {
					float scale = 0.3F;
					float off = (1F - scale) / 2;
					RenderSystem.translatef(off, off, -off);
					RenderSystem.scalef(scale, scale, scale);

					brd.renderBlockBrightness(Blocks.RED_CONCRETE.getDefaultState(), 1.0F);


				} else brd.renderBlockBrightness(state, 1.0F);
			} catch(NullPointerException e) { 
				//  This can crash for some reason and idk why so this is a bandaid fix
				BufferBuilder builder = Tessellator.getInstance().getBuffer(); 
				builder.reset();
				builder.finishDrawing();
			}

			RenderSystem.popMatrix();
		}
	}
	 */

	public static IMultiblock getMultiblock() {
		return multiblock;
	}

	public static boolean isAnchored() {
		return isAnchored;
	}

	public static BlockRotation getFacingBlockRotation() {
		return multiblock.isSymmetrical() ? BlockRotation.NONE : facingBlockRotation;
	}

	public static BlockPos getStartPos() {
		return offsetApplier.apply(pos);
	}

	private static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float)(startColor >> 24 & 255) / 255.0F;
		float f1 = (float)(startColor >> 16 & 255) / 255.0F;
		float f2 = (float)(startColor >> 8 & 255) / 255.0F;
		float f3 = (float)(startColor & 255) / 255.0F;
		float f4 = (float)(endColor >> 24 & 255) / 255.0F;
		float f5 = (float)(endColor >> 16 & 255) / 255.0F;
		float f6 = (float)(endColor >> 8 & 255) / 255.0F;
		float f7 = (float)(endColor & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferbuilder.vertex((double)right, (double)top, 0).color(f1, f2, f3, f).next();
		bufferbuilder.vertex((double)left, (double)top, 0).color(f1, f2, f3, f).next();
		bufferbuilder.vertex((double)left, (double)bottom, 0).color(f5, f6, f7, f4).next();
		bufferbuilder.vertex((double)right, (double)bottom, 0).color(f5, f6, f7, f4).next();
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
	 */
	private static BlockRotation getBlockRotation(Entity entity) {
		return RotationUtil.rotationFromFacing(entity.getHorizontalFacing());
	}

}
