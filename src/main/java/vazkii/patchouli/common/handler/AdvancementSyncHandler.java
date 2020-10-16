package vazkii.patchouli.common.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

import java.util.HashSet;
import java.util.Set;

public final class AdvancementSyncHandler {
	private static final Set<EntityPlayerMP> playersToUpdate = new HashSet<>();

	@SubscribeEvent
	public static void onAdvancement(AdvancementEvent event) {
		if (event.getEntityPlayer() instanceof EntityPlayerMP) {
			// Let's not send 500 packets if you do /advancement grant @p everything
			playersToUpdate.add(((EntityPlayerMP) event.getEntityPlayer()));
		}
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && !playersToUpdate.isEmpty()) {
			for (EntityPlayerMP player : playersToUpdate) {
				syncPlayer(player, true);
			}
			playersToUpdate.clear();
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerLoggedInEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		// Vanilla loads advancements on first tick after join, so we have to force it to do it a bit earlier.
		event.player.world.getMinecraftServer().getPlayerList().getPlayerAdvancements(player).flushDirty(player);
		syncPlayer(player, false);
	}

	public static void syncPlayer(EntityPlayerMP player, boolean showToast) {
		NetworkHandler.INSTANCE.sendTo(new MessageSyncAdvancements(showToast), player);
	}
}
