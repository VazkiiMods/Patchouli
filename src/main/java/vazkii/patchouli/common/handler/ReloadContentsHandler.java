package vazkii.patchouli.common.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;

import vazkii.patchouli.common.network.message.MessageReloadBookContents;
import vazkii.patchouli.mixin.AccessorMinecraftServer;

public class ReloadContentsHandler {
	public static void init() {
		ServerLifecycleEvents.SERVER_STARTED.register(ReloadContentsHandler::serverStart);
	}

	private static void serverStart(MinecraftServer server) {
		// Also reload contents when someone types /reload
		SynchronousResourceReloadListener listener = m -> MessageReloadBookContents.sendToAll(server);
		((ReloadableResourceManager) ((AccessorMinecraftServer) server).getServerResourceManager().getResourceManager())
				.registerListener(listener);
	}
}
