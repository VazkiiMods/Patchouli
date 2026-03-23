package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public final class MultiblockPiPRenderer extends PictureInPictureRenderer<MultiblockPiPRenderState> {
	private final Minecraft mc;
	private final SubmitNodeCollector submitNodeCollector;
	private final BlockEntityRenderDispatcher blockEntityRenderer;

	public MultiblockPiPRenderer(MultiBufferSource.BufferSource bufferSource, Minecraft mc, SubmitNodeCollector submitNodeCollector) {
		super(bufferSource);
		this.mc = mc;
		this.submitNodeCollector = submitNodeCollector;
		this.blockEntityRenderer = mc.getBlockEntityRenderDispatcher();
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
		/*MultiblockTintWrapper multiblock = new MultiblockTintWrapper(renderState.multiblock());
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
		
		BlockStateModelSet blockStateModelSet = mc.getModelManager().getBlockStateModelSet();
		ModelBlockRenderer blockRenderer = new ModelBlockRenderer(false, true, mc.getBlockColors());
		FluidRenderer fluidRenderer = new FluidRenderer(mc.getModelManager().getFluidStateModelSet());
		
		Map<ChunkSectionLayer, ByteBufferBuilder> builders = Util.makeEnumMap(ChunkSectionLayer.class, (layer) -> new ByteBufferBuilder(layer.bufferSize()));
		Map<ChunkSectionLayer, BufferBuilder> startedLayers = new EnumMap<>(ChunkSectionLayer.class);
		BlockQuadOutput quadOutput = (x, y, z, quad, instance) -> getOrBeginLayer(startedLayers, builders, quad.materialInfo().layer()).putBlockBakedQuad(x, y, z, quad, instance);
		FluidRenderer.Output fluidOutput = layerx -> getOrBeginLayer(startedLayers, builders, layerx);
		
		CameraRenderState cameraRenderState = new CameraRenderState();
		
		for (BlockPos pos : BlockPos.betweenClosed(bounds)) {
			BlockState blockstate = multiblock.getBlockState(pos);
		
			if (blockstate.isAir()) continue;
		
			if (blockstate.hasBlockEntity()) {
				BlockEntity blockentity = multiblock.getBlockEntity(pos);
				if (blockentity != null) {
					this.handleBlockEntity(blockentity, poseStack, cameraRenderState);
				}
			}
		
			FluidState fluidstate = blockstate.getFluidState();
			if (!fluidstate.isEmpty()) {
				fluidRenderer.tesselate(multiblock, pos, layer -> new LiquidBlockVertexConsumer(fluidOutput.getBuilder(layer), poseStack, pos), blockstate, fluidstate);
			}
		
			if (blockstate.getRenderShape() == RenderShape.MODEL) {
				blockRenderer.tesselateBlock(quadOutput, 0, 0, 0, multiblock, pos, blockstate, blockStateModelSet.get(blockstate), 0L);
			}
		}
		
		poseStack.popPose();*/
	}

	private static BufferBuilder getOrBeginLayer(Map<ChunkSectionLayer, BufferBuilder> startedLayers, Map<ChunkSectionLayer, ByteBufferBuilder> buffers, ChunkSectionLayer layer) {
		BufferBuilder builder = startedLayers.get(layer);
		if (builder == null) {
			ByteBufferBuilder buffer = buffers.get(layer);
			builder = new BufferBuilder(buffer, VertexFormat.Mode.QUADS, layer.vertexFormat());
			startedLayers.put(layer, builder);
		}

		return builder;
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
