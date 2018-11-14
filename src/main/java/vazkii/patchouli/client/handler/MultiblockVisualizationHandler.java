package vazkii.patchouli.client.handler;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.common.util.ReflectionUtil;
import vazkii.patchouli.common.multiblock.Multiblock;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;

public class MultiblockVisualizationHandler {

	public static boolean hasMultiblock;
	public static Bookmark bookmark;
	
	private static Multiblock multiblock;
	private static String name;
	private static BlockPos pos;
	private static boolean isAnchored;
	private static Rotation facingRotation;
	private static Function<BlockPos, BlockPos> offsetApplier;
	private static int blocks, blocksDone, airFilled;
	private static int timeComplete;
	private static IBlockState lookingState;
	private static BlockPos lookingPos;

	private static Field fieldRenderPosX;
	private static Field fieldRenderPosY;
	private static Field fieldRenderPosZ;

	static {
		try {
			fieldRenderPosX = ReflectionUtil.accessField(RenderManager.class, "field_78725_b", "D");
			fieldRenderPosY = ReflectionUtil.accessField(RenderManager.class, "field_78726_c", "D");
			fieldRenderPosZ = ReflectionUtil.accessField(RenderManager.class, "field_78723_d", "D");
		} catch (NoSuchFieldException e) {
		    throw new RuntimeException("Unable to find necessary fields", e);
		}
	}

	public static void setMultiblock(Multiblock multiblock, String name, Bookmark bookmark, boolean flip) {
		setMultiblock(multiblock, name, bookmark, flip, pos->pos);
	}
	
	public static void setMultiblock(Multiblock multiblock, String name, Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
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

	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.ALL && hasMultiblock) {
			int waitTime = 40;
			int fadeOutSpeed = 4;
			int fullAnimTime = waitTime + 10;
			float animTime = timeComplete + (timeComplete == 0 ? 0 : event.getPartialTicks());

			if(animTime > fullAnimTime) {
				hasMultiblock = false;
				return;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

			int x = event.getResolution().getScaledWidth() / 2;
			int y = 12;

			Minecraft mc = Minecraft.getMinecraft();
			mc.fontRenderer.drawStringWithShadow(name, x - mc.fontRenderer.getStringWidth(name) / 2, y , 0xFFFFFF);

			int width = 180;
			int height = 9;
			int left = x - width / 2;
			int top = y + 10;

			if(timeComplete > 0) {
				String s = I18n.translateToLocal("patchouli.gui.lexicon.structure_complete");
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, Math.min(height + 5, animTime), 0);
				mc.fontRenderer.drawStringWithShadow(s, x - mc.fontRenderer.getStringWidth(s) / 2, top + height - 10, 0x00FF00);
				GlStateManager.popMatrix();
			}

			Gui.drawRect(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
			drawGradientRect(left, top, left + width, top + height, 0xFF666666, 0xFF555555);

			float fract = (float) blocksDone / Math.max(1, blocks);
			int progressWidth = (int) ((float) width * fract);
			int color = MathHelper.hsvToRGB(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
			int color2 = new Color(color).darker().getRGB();
			drawGradientRect(left, top, left + progressWidth, top + height, color, color2);

			if(!isAnchored) {
				String s = I18n.translateToLocal("patchouli.gui.lexicon.not_anchored");
				mc.fontRenderer.drawStringWithShadow(s, x - mc.fontRenderer.getStringWidth(s) / 2, top + height + 8, 0xFFFFFF);
			} else {
				if(lookingState != null) {
					Block block = lookingState.getBlock();
					ItemStack stack = block.getPickBlock(lookingState, mc.objectMouseOver, mc.world, lookingPos, mc.player);

					if(!stack.isEmpty()) {
						mc.fontRenderer.drawStringWithShadow(stack.getDisplayName(), left + 20, top + height + 8, 0xFFFFFF);
						RenderHelper.enableGUIStandardItemLighting();
						mc.getRenderItem().renderItemIntoGUI(stack, left, top + height + 2);
					}
				}

				if(timeComplete == 0) {
					color = 0xFFFFFF;
					int posx = left + width;
					int posy = top + height + 2;
					int mult = 1;
					String progress = blocksDone + "/" + blocks;
					
					if(blocksDone == blocks && airFilled > 0) {
						progress = I18n.translateToLocal("patchouli.gui.lexicon.needs_air");
						color = 0xDA4E3F;
						mult *= 2;
						posx -= width / 2;
						posy += 2;
					}
					
					mc.fontRenderer.drawStringWithShadow(progress, posx - mc.fontRenderer.getStringWidth(progress) / mult, posy, color);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent event) {
		if(hasMultiblock && multiblock != null)
			renderMultiblock(Minecraft.getMinecraft().world);
	}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if(hasMultiblock && !isAnchored) {
			pos = event.getPos();
			facingRotation = RotationUtil.rotationFromFacing(event.getEntityPlayer().getHorizontalFacing());
			isAnchored = true;
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(Minecraft.getMinecraft().world == null)
			hasMultiblock = false;
		else if(isAnchored && blocks == blocksDone && airFilled == 0) {
			timeComplete++;
			if(timeComplete == 14)
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
		} else timeComplete = 0;
	}

	public static void renderMultiblock(World world) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!isAnchored) {
			facingRotation = RotationUtil.rotationFromFacing(mc.player.getHorizontalFacing());
			if(mc.objectMouseOver != null)
				pos = mc.objectMouseOver.getBlockPos();
		}
		else if(mc.player.getDistanceSq(pos) > 64 * 64)
			return;

		if(pos == null)
			return;
		if(multiblock.isSymmetrical())
			facingRotation = Rotation.NONE;

		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		double posX = 0;
		double posY = 0;
		double posZ = 0;
		try {
			posX = (Double)fieldRenderPosX.get(manager);
			posY = (Double)fieldRenderPosY.get(manager);
			posZ = (Double)fieldRenderPosZ.get(manager);
		} catch (IllegalAccessException ignored) { }

		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.translate(-posX, -posY, -posZ);

		BlockPos checkPos = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == Type.BLOCK ? mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit) : null;
		BlockPos startPos = getStartPos();
		
		blocks = blocksDone = airFilled = 0;
		lookingState = null;
		lookingPos = checkPos;

		for(int x = 0; x < multiblock.sizeX; x++)
			for(int y = 0; y < multiblock.sizeY; y++)
				for(int z = 0; z < multiblock.sizeZ; z++) {
					float alpha = 0.3F;
					BlockPos renderPos = startPos.add(RotationUtil.x(facingRotation, x, z) , y, RotationUtil.z(facingRotation, x, z));
					IStateMatcher matcher = multiblock.stateTargets[x][y][z];
					if(renderPos.equals(checkPos)) {
						lookingState = matcher.getDisplayedState();
						alpha = 0.6F + (float) (Math.sin(ClientTicker.total * 0.3F) + 1F) * 0.1F;
					}

					if(matcher != StateMatcher.ANY) {
						boolean air = matcher == StateMatcher.AIR;
						if(!air)
							blocks++;
						
						if(!multiblock.test(world, startPos, x, y, z, facingRotation)) {
							IBlockState renderState = matcher.getDisplayedState().withRotation(facingRotation);
							mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
							renderBlock(world, renderState, renderPos, alpha, dispatcher);

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
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	public static void renderBlock(World world, IBlockState state, BlockPos pos, float alpha, BlockRendererDispatcher brd) {
		if(pos != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			GL14.glBlendColor(1F, 1F, 1F, alpha);

			if(state.getBlock() == Blocks.AIR) {
				float scale = 0.3F;
				float off = (1F - scale) / 2;
				GlStateManager.translate(off, off, -off);
				GlStateManager.scale(scale, scale, scale);

				brd.renderBlockBrightness(Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED), 1.0F);
			} else brd.renderBlockBrightness(state, 1.0F);

			GlStateManager.popMatrix();
		}
	}

	public static Multiblock getMultiblock() {
		return multiblock;
	}
	
	public static boolean isAnchored() {
		return isAnchored;
	}
	
	public static Rotation getFacingRotation() {
		return multiblock.isSymmetrical() ? Rotation.NONE : facingRotation;
	}
	
	public static BlockPos getStartPos() {
		Rotation rot = getFacingRotation();
		BlockPos startPos = offsetApplier.apply(pos);
		startPos = startPos.add(-RotationUtil.x(rot, multiblock.viewOffX, multiblock.viewOffZ), -multiblock.viewOffY + 1, -RotationUtil.z(rot, multiblock.viewOffX, multiblock.viewOffZ));
		return startPos;
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
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
	
}
