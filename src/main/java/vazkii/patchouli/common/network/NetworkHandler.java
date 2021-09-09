package vazkii.patchouli.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Patchouli.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int i = 0;

	private static synchronized int nextId() {
		return i++;
	}

	public static void registerMessages() {
		CHANNEL.registerMessage(nextId(), MessageOpenBookGui.class, MessageOpenBookGui::encode, MessageOpenBookGui::decode, MessageOpenBookGui::handle);
		CHANNEL.registerMessage(nextId(), MessageReloadBookContents.class, MessageReloadBookContents::encode, MessageReloadBookContents::decode, MessageReloadBookContents::handle);
	}
}
