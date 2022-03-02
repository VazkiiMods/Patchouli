package vazkii.patchouli.common.handler;

import net.minecraft.server.MinecraftServer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.IXplatAbstractions;

public class ReloadContentsHandler {
	public static void dataReloaded(MinecraftServer server) {
		// Also reload contents when someone types /reload
		PatchouliAPI.LOGGER.info("Sending reload packet to clients");
		IXplatAbstractions.INSTANCE.sendReloadContentsMessage(server);
	}
}
