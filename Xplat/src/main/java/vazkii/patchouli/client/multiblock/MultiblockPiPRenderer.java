package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

// Code adapted from EnderIO
public final class MultiblockPiPRenderer extends PictureInPictureRenderer<MultiblockPiPRenderState> {

	public MultiblockPiPRenderer(MultiBufferSource.BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<MultiblockPiPRenderState> getRenderStateClass() {
		return MultiblockPiPRenderState.class;
	}

	@Override
	protected void renderToTexture(MultiblockPiPRenderState renderState, PoseStack poseStack) {
		poseStack.pushPose();
		poseStack.mulPose(renderState.viewMatrix());
		Minecraft minecraft = Minecraft.getInstance();
		GameRenderer gameRenderer = minecraft.gameRenderer;
		FeatureRenderDispatcher featureRenderDispatcher = gameRenderer.getFeatureRenderDispatcher();
		SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
		gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
		for (var block : renderState.multiblock()) {
			poseStack.pushPose();
			poseStack.translate(block.pos().getCenter());
			block.blockModelRenderState().submit(poseStack, submitNodeStorage, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
		featureRenderDispatcher.renderAllFeatures();
		this.bufferSource.endBatch();
		poseStack.popPose();
	}

	@Override
	protected String getTextureLabel() {
		return "patchouli_multiblock";
	}
}
