package vazkii.patchouli.client.mixin;

import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(ClientAdvancementManager.class)
public abstract class MixinClientAdvancementManager {

	@Inject(at = @At("RETURN"), method = "onAdvancement")
	public void patchouli_onAdvancement(AdvancementUpdateS2CPacket packet, CallbackInfo info) {
		ClientAdvancements.onClientPacket();
	}
}
