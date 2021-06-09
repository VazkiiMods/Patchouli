package vazkii.patchouli.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class NetworkHandler {

	public static void registerMessages() {
		ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.ID, MessageOpenBookGui::handle);
		ClientPlayNetworking.registerGlobalReceiver(MessageReloadBookContents.ID, MessageReloadBookContents::handle);
	}

}
