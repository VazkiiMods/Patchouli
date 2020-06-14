package vazkii.patchouli.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class RenderHelper {
	/**
	 * Temporary shim to allow {@link net.minecraft.client.render.item.ItemRenderer#renderInGuiWithOverrides}
	 * to support matrixstack transformations. Hopefully Mojang finishes this migration up...
	 * Assumption: the "root" state of the MatrixStack is same as the currently GL state,
	 * such that multiplying the MatrixStack to the current GL matrix state will get us where we want to be.
	 *
	 * If there have been intervening dirty changes to the GL matrix state, then this won't work.
	 */
	public static void renderItemStackInGui(MatrixStack ms, ItemStack stack, int x, int y) {
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(ms.peek().getModel());
		MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(stack, x, y);
		RenderSystem.popMatrix();
	}
}
