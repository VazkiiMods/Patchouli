package vazkii.patchouli.common.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class ReloadContentsHandler {
	public static void init() {
		ServerLifecycleEvents.SERVER_STARTED.register(ReloadContentsHandler::serverStart);
	}

	private static void serverStart(MinecraftServer server) {
		// Also reload contents when someone types /reload
		ResourceManagerReloadListener listener = m -> MessageReloadBookContents.sendToAll(server);
		((ReloadableResourceManager) server.getResourceManager()).registerReloadListener(listener);
	}
}
