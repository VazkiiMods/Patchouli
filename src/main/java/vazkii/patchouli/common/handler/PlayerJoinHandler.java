package vazkii.patchouli.common.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import vazkii.patchouli.common.base.Patchouli;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID)
public class PlayerJoinHandler {
	@SubscribeEvent
	public static void playerLogin(PlayerEvent.PlayerLoggedInEvent evt) {
		/* Advancements are not synced immediately on login, but on the player's first server tick.
		* Thus, there is a short time clientside where the world is active and rendering but book contents
		* are not loaded (because we load when advancements do).
		* This can cause crashes for multiple reasons.
		* Since this method is idempotent and the advancements are already loaded serverside, it should be safe
		* to call this a bit earlier than vanilla does.
		* Ideally, we want to make it so that any attempts to access the books cleanly receive dummy/blank data
		* until the contents load, but alas here we are.
		* TODO: Do that^
		*/
		((ServerPlayerEntity) evt.getPlayer()).getAdvancements().flushDirty((ServerPlayerEntity) evt.getPlayer());
	}
}
