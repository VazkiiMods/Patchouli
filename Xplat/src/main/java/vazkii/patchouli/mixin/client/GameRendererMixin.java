package vazkii.patchouli.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.handler.MultiblockGhostHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "pick(F)V", // pick(float)
            at = @At("HEAD")
    )
    private void patchouli$pickGhost(float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!MultiblockVisualizationHandler.hasMultiblock) {
            MultiblockGhostHandler.reset();
            return;
        }
        if (mc.player == null) {
            MultiblockGhostHandler.reset();
            return;
        }

        // Attempt ghost-block raycast
        var ghostHit = MultiblockGhostHandler.raycastGhost(mc.player, partialTicks);
        MultiblockGhostHandler.updateHit(ghostHit);
    }
}