package vazkii.patchouli.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.TooltipHandler;

@Mixin(Screen.class)
public class MixinScreen {
	@Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V")
	public void patchouli_onRenderTooltip(ItemStack stack, int x, int y, CallbackInfo info) {
		TooltipHandler.onTooltip(stack, x, y);
	}
}
