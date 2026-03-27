package vazkii.patchouli.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.TooltipHandler;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"), method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
	public void patchouli_onExtractTooltip(GuiGraphicsExtractor extractor, int x, int y, CallbackInfo info, @Local ItemStack stack) {
		TooltipHandler.onTooltip(extractor, stack, x, y);
	}
}
