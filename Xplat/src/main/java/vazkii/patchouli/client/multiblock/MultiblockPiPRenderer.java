package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

// Code adapted from EnderIO
public final class MultiblockPiPRenderer extends PictureInPictureRenderer<MultiblockPiPRenderState> {
	private final SubmitNodeCollector submitNodeCollector;
	private final FeatureRenderDispatcher featureRenderDispatcher;
	private final GameRenderer gameRenderer;

	public MultiblockPiPRenderer(MultiBufferSource.BufferSource bufferSource) {
		super(bufferSource);
		this.gameRenderer = Minecraft.getInstance().gameRenderer;
		this.featureRenderDispatcher = this.gameRenderer.getFeatureRenderDispatcher();
		this.submitNodeCollector = this.featureRenderDispatcher.getSubmitNodeStorage();
	}

	@Override
	public Class<MultiblockPiPRenderState> getRenderStateClass() {
		return MultiblockPiPRenderState.class;
	}

	@Override
	protected void renderToTexture(MultiblockPiPRenderState renderState, PoseStack poseStack) {
		poseStack.pushPose();
		poseStack.mulPose(renderState.viewMatrix());
		this.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		for (var block : renderState.multiblock()) {
			poseStack.pushPose();
			poseStack.translate(new Vec3(block.pos()));
			block.blockModelRenderState().submit(poseStack, this.submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
		this.featureRenderDispatcher.renderAllFeatures();
		this.bufferSource.endBatch();
		poseStack.popPose();
	}

	@Override
	protected String getTextureLabel() {
		return "patchouli_multiblock";
	}
}
