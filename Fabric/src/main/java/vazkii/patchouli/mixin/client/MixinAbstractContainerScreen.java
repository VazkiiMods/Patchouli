package vazkii.patchouli.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.inventory.Slot;
import vazkii.patchouli.client.handler.TooltipHandler;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen{

    @Shadow
    Slot hoveredSlot;

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    public void patchouli_onRenderTooltip(GuiGraphics guiGraphics,  int x, int y, CallbackInfo info) {
        if (((AbstractContainerScreen) (Object) this).getMenu().getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
            TooltipHandler.onTooltip(guiGraphics, this.hoveredSlot.getItem(), x, y);
        }
    }
}
