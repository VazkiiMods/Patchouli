package vazkii.patchouli.common.handler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.XplatAbstractions;

public class ReloadContentsHandler {
	public static void dataReloaded(MinecraftServer server, ServerResources serverResourceManager, boolean success) {
		// Also reload contents when someone types /reload
		if (success) {
			PatchouliAPI.LOGGER.info("Sending reload packet to clients");
			XplatAbstractions.getInstance().sendReloadContentsMessage(server);
		}
	}
}
