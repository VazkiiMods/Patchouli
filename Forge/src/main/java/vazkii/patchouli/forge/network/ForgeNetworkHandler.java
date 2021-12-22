package vazkii.patchouli.forge.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import vazkii.patchouli.api.PatchouliAPI;

public class ForgeNetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(PatchouliAPI.MOD_ID, "main"))
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.simpleChannel();

	public static void registerMessages() {
		int i = 0;
		CHANNEL.registerMessage(i++, ForgeMessageOpenBookGui.class, ForgeMessageOpenBookGui::encode, ForgeMessageOpenBookGui::decode, ForgeMessageOpenBookGui::handle);
		CHANNEL.registerMessage(i++, ForgeMessageReloadBookContents.class, ForgeMessageReloadBookContents::encode, ForgeMessageReloadBookContents::decode, ForgeMessageReloadBookContents::handle);
	}
}
