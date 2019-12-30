package vazkii.patchouli.client.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
    @Inject(at = @At("RETURN"), method = "render(F)V")
    public void afterRender(float partialTicks, CallbackInfo info) {
        BookRightClickHandler.onRenderHUD();
        MultiblockVisualizationHandler.onRenderHUD(partialTicks);
    }
}
