package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.SectionPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

// Code adapted from EnderIO
public final class MultiblockPiPRenderer extends PictureInPictureRenderer<MultiblockPiPRenderState> {
	private final SubmitNodeCollector submitNodeCollector;
	private final FeatureRenderDispatcher featureRenderDispatcher;

	public MultiblockPiPRenderer(MultiBufferSource.BufferSource bufferSource, Minecraft mc, SubmitNodeCollector submitNodeCollector) {
		super(bufferSource);
		this.submitNodeCollector = submitNodeCollector;
		this.featureRenderDispatcher = mc.gameRenderer.getFeatureRenderDispatcher();
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
		poseStack.pushPose();
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		for (var block : renderState.multiblock()) {
			renderBlock(poseStack, block);
		}
		featureRenderDispatcher.renderAllFeatures();
		bufferSource.endBatch();
		poseStack.popPose();
	}

	private void renderBlock(PoseStack poseStack, MultiblockPiPRenderState.BlockRenderState block) {
		poseStack.pushPose();
		poseStack.translate(new Vec3(block.pos()));

		block.blockModelRenderState().submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

		poseStack.popPose();
	}

	@Override
	protected String getTextureLabel() {
		return "patchouli_multiblock";
	}
}
