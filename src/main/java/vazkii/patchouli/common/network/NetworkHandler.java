package vazkii.patchouli.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
		.named(new ResourceLocation(Patchouli.MOD_ID, "main"))
		.networkProtocolVersion(() -> PROTOCOL_VERSION)
		.clientAcceptedVersions(PROTOCOL_VERSION::equals)
		.serverAcceptedVersions(PROTOCOL_VERSION::equals)
		.simpleChannel();

	public static void registerMessages() {
		int i = 0;
		CHANNEL.registerMessage(i++, MessageOpenBookGui.class, MessageOpenBookGui::encode, MessageOpenBookGui::decode, MessageOpenBookGui::handle);
		CHANNEL.registerMessage(i++, MessageReloadBookContents.class, MessageReloadBookContents::encode, MessageReloadBookContents::decode, MessageReloadBookContents::handle);
	}

}
