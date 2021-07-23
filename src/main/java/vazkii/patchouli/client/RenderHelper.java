package vazkii.patchouli.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class RenderHelper {
	public static void renderItemStackInGui(PoseStack ms, ItemStack stack, int x, int y) {
		transferMsToGl(ms, () -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y));
	}

	/**
	 * Temporary shim to allow methods such as
	 * {@link net.minecraft.client.renderer.entity.ItemRenderer#renderAndDecorateItem}
	 * to support matrixstack transformations. Hopefully Mojang finishes this migration up...
	 * Transfers the current CPU matrixstack to the openGL matrix stack, then runs the provided function
	 * Assumption: the "root" state of the MatrixStack is same as the currently GL state,
	 * such that multiplying the MatrixStack to the current GL matrix state will get us where we want to be.
	 * If there have been intervening changes to the GL matrix state since the MatrixStack was constructed, then this
	 * won't work.
	 */
	public static void transferMsToGl(PoseStack ms, Runnable toRun) {
		PoseStack mvs = RenderSystem.getModelViewStack();
		mvs.pushPose();
		mvs.mulPoseMatrix(ms.last().pose());
		RenderSystem.applyModelViewMatrix();
		toRun.run();
		mvs.popPose();
		RenderSystem.applyModelViewMatrix();
	}
}
