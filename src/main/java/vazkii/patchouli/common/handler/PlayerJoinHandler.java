package vazkii.patchouli.common.handler;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerJoinHandler {
	public static void playerLogin(ServerPlayerEntity player) {
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
		player.getAdvancementTracker().sendUpdate(player);
	}
}
