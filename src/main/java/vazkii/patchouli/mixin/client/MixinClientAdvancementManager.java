package vazkii.patchouli.mixin.client;

import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.network.play.server.SAdvancementInfoPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(ClientAdvancementManager.class)
public class MixinClientAdvancementManager {

	@Inject(at = @At("RETURN"), method = "read")
	public void patchouli_onSync(SAdvancementInfoPacket packet, CallbackInfo info) {
		ClientAdvancements.onClientPacket();
	}
}
