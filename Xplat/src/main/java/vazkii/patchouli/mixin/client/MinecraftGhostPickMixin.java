package vazkii.patchouli.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.handler.MultiblockGhostHandler;

@Mixin(Minecraft.class)
public class MinecraftGhostPickMixin {

	@SuppressWarnings("resource")
	@Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
	private void patchouli$handleGhostPick(CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		Player player = mc.player;
		if (player == null) {
			return;
		}

		HitResult current = mc.hitResult;
		BlockHitResult realHit = current instanceof BlockHitResult bhr ? bhr : null;
		BlockHitResult adjusted = MultiblockGhostHandler.getAdjustedHitResult(player, realHit);
		if (adjusted != null && adjusted != realHit && MultiblockGhostHandler.canPick()) {
			if (MultiblockGhostHandler.handleMiddleClick(player)) {
				ci.cancel();
			}
		}
	}
}
