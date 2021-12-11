package vazkii.patchouli.common.handler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;

import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class ReloadContentsHandler {
	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(ReloadContentsHandler::serverStart);
	}

	private static void serverStart(ServerStartedEvent evt) {
		MinecraftServer server = evt.getServer();
		// Also reload contents when someone types /reload
		ResourceManagerReloadListener listener = m -> MessageReloadBookContents.sendToAll(server);
		((ReloadableResourceManager) server.getResourceManager()).registerReloadListener(listener);
	}
}
