package vazkii.patchouli.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.gui.GoVoteHandler;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void patchouli_onLogout(Screen screen, CallbackInfo info) {
		ClientAdvancements.playerLogout();
	}

	@Inject(at = @At("HEAD"), method = "openScreen", cancellable = true)
	public void patchouli_handleGoVote(Screen screen, CallbackInfo ci) {
		if (GoVoteHandler.guiOpen(screen)) {
			ci.cancel();
		}
	}
}
