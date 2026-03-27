package vazkii.patchouli.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.TooltipHandler;

@Mixin(GuiGraphicsExtractor.class)
public class MixinGuiGraphicsExtractor {
	// Inject into setTooltipForNextFrame(Font font, ItemStack itemStack, int xo, int yo) at the head
	@Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	public void patchouli_onSetTooltipForNextFrame(Font font, ItemStack itemStack, int xo, int yo, CallbackInfo ci) {
		TooltipHandler.onTooltip((GuiGraphicsExtractor) (Object) this, itemStack, xo, yo);
	}
}
