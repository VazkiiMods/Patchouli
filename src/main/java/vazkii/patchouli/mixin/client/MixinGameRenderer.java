package vazkii.patchouli.mixin.client;

import net.minecraft.client.render.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientTicker;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Inject(at = @At("HEAD"), method = "render(FJZ)V")
	public void patchouli_renderStart(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
		ClientTicker.renderTickStart(tickDelta);
	}

	@Inject(at = @At("RETURN"), method = "render(FJZ)V")
	public void patchouli_renderEnd(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
		ClientTicker.renderTickEnd();
	}

}
