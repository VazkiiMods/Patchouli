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
public class MixinGuiGraphics {
	@Inject(at = @At("HEAD"), method = "tooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
	public void patchouli_onRenderTooltip(Font font, ItemStack stack, int x, int y, CallbackInfo info) {
		TooltipHandler.onTooltip((GuiGraphicsExtractor) (Object) this, stack, x, y);
	}
}
