package vazkii.patchouli.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "pick(F)V", // pick(float)
            at = @At("HEAD"),
            cancellable = true
    )
    private void patchouli$pickGhost(float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!MultiblockVisualizationHandler.hasMultiblock) return;

        // Attempt ghost-block raycast
        HitResult ghostHit = MultiblockVisualizationHandler.raycastGhost(mc.player, partialTicks);
        if (ghostHit == null) return;

        // Override the hit result so it *is* the ghost block
        mc.hitResult = ghostHit;

        // Cancel so vanilla does not overwrite mc.hitResult
        ci.cancel();
    }
}