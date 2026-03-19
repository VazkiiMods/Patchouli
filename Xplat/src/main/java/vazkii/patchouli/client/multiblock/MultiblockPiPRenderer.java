package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

import vazkii.patchouli.client.book.LiquidBlockVertexConsumer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.function.Function;

public class MultiblockPiPRenderer extends PictureInPictureRenderer<MultiblockPiPRenderState> {
	private static final RandomSource RAND = RandomSource.createNewThreadLocalInstance();
	private final Minecraft mc;
	private final SubmitNodeCollector submitNodeCollector;
	private final BlockEntityRenderDispatcher blockEntityRenderer;
	private final BlockRenderDispatcher blockRenderer;

	public MultiblockPiPRenderer(MultiBufferSource.BufferSource bufferSource, Minecraft mc, SubmitNodeCollector submitNodeCollector) {
		super(bufferSource);
		this.mc = mc;
		this.submitNodeCollector = submitNodeCollector;
		this.blockEntityRenderer = mc.getBlockEntityRenderDispatcher();
		this.blockRenderer = mc.getBlockRenderer();
	}

	@Override
	public Class<MultiblockPiPRenderState> getRenderStateClass() {
		return MultiblockPiPRenderState.class;
	}

	/**
	 * @implNote Adapted from
	 *           {@link SectionCompiler#compile(SectionPos, RenderSectionRegion, VertexSorting, SectionBufferBuilderPack)}
	 */
	@Override
	protected void renderToTexture(MultiblockPiPRenderState renderState, PoseStack poseStack) {
		AbstractMultiblock multiblock = renderState.multiblock();
		multiblock.setWorld(mc.level);
		AABB bounds = multiblock.getBounds();
		double sizeX = bounds.getXsize();
		double sizeY = bounds.getYsize();
		double sizeZ = bounds.getZsize();
		double maxX = 90;
		double maxY = 90;
		double diag = Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
		double scaleX = maxX / diag;
		double scaleY = maxY / sizeY;
		float scale = (float) -Math.min(scaleX, scaleY);

		int xPos = GuiBook.PAGE_WIDTH / 2;
		int yPos = 60;
		poseStack.pushPose();
		poseStack.translate(xPos, yPos, 0);
		poseStack.scale(scale, scale, scale);
		poseStack.translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);

		poseStack.mulPose(Axis.XP.rotationDegrees(-30F));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		poseStack.translate(-offX, 0, -offZ);
		poseStack.mulPose(renderState.rotation());
		poseStack.translate(offX, 0, offZ);

		Function<ChunkSectionLayer, VertexConsumer> bufferLookup = layer -> bufferSource.getBuffer(layer != ChunkSectionLayer.TRANSLUCENT ? Sheets.cutoutBlockSheet() : Sheets.translucentBlockItemSheet());
		CameraRenderState cameraRenderState = new CameraRenderState();

		for (BlockPos pos : BlockPos.betweenClosed(bounds)) {
			BlockState blockstate = multiblock.getBlockState(pos);

			if (blockstate.hasBlockEntity()) {
				BlockEntity blockentity = multiblock.getBlockEntity(pos);
				if (blockentity != null) {
					this.handleBlockEntity(blockentity, poseStack, cameraRenderState);
				}
			}

			FluidState fluidstate = blockstate.getFluidState();
			if (!fluidstate.isEmpty()) {
				ChunkSectionLayer layer = ItemBlockRenderTypes.getRenderLayer(fluidstate);
				blockRenderer.renderLiquid(pos, multiblock, new LiquidBlockVertexConsumer(bufferLookup.apply(layer), poseStack, pos), blockstate, fluidstate);
			}

			if (blockstate.getRenderShape() == RenderShape.MODEL) {
				RAND.setSeed(blockstate.getSeed(pos));
				IClientXplatAbstractions.INSTANCE.renderForMultiblock(blockRenderer, blockstate, pos, multiblock, poseStack, bufferLookup, RAND);
			}
		}

		poseStack.popPose();
	}

	private <E extends BlockEntity, S extends BlockEntityRenderState> void handleBlockEntity(E blockEntity, PoseStack poseStack, CameraRenderState cameraRenderState) {
		BlockEntityRenderer<E, S> renderer = blockEntityRenderer.getRenderer(blockEntity);
		if (renderer == null) {
			return;
		}
		S renderState = renderer.createRenderState();
		renderer.extractRenderState(blockEntity, renderState, 0, cameraRenderState.pos, null);
		renderer.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
	}

	@Override
	protected String getTextureLabel() {
		return "patchouli_multiblock";
	}
}
