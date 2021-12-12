package vazkii.patchouli.common.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class ReloadContentsHandler {
	public static void init() {
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ReloadContentsHandler::dataReloaded);
	}

	private static void dataReloaded(MinecraftServer server, ServerResources serverResourceManager, boolean success) {
		// Also reload contents when someone types /reload
		if (success) {
			Patchouli.LOGGER.info("Sending reload packet to clients");
			MessageReloadBookContents.sendToAll(server);
		}
	}
}
