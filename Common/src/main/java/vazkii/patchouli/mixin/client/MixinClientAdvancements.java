package vazkii.patchouli.mixin.client;

import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(net.minecraft.client.multiplayer.ClientAdvancements.class)
public abstract class MixinClientAdvancements {

	@Inject(at = @At("RETURN"), method = "update")
	public void patchouli_onSync(ClientboundUpdateAdvancementsPacket packet, CallbackInfo info) {
		ClientAdvancements.onClientPacket();
	}
}
