package vazkii.patchouli.mixin.client;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;


import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

	@Inject(at = @At("RETURN"), method = "renderLevel")
	public void onRender(GraphicsResourceAllocator graphicsResourceAllocator,
						 DeltaTracker deltaTracker,
						 boolean renderBlockOutline,
						 Camera camera,
						 GameRenderer gameRenderer,
						 Matrix4f frustumMatrix,
						 Matrix4f projectionMatrix,
						 CallbackInfo ci) {
		PoseStack poseStack = new PoseStack();

		// inverse camera rotation
		org.joml.Quaternionf camRot = new org.joml.Quaternionf(camera.rotation());
		camRot.conjugate();
		poseStack.mulPose(camRot);

		// inverse camera position
		Vec3 camPos = camera.getPosition();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		MultiblockVisualizationHandler.onWorldRenderLast(poseStack);
	}
}
