package vazkii.patchouli.common.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;

@Mixin(PlayerAdvancementTracker.class)
public class MixinPlayerAdvancementTracker {
    @Shadow @Final private ServerPlayerEntity owner;

    @Inject(at = @At("RETURN"), method = "endTrackingCompleted(Lnet/minecraft/advancement/Advancement;)V")
    public void onAdvancement(Advancement adv, CallbackInfo info) {
        AdvancementSyncHandler.onAdvancement(owner, adv);
    }
}
