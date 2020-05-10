/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 * File Created @ [11/01/2016, 21:58:25 (GMT)]
 */
package vazkii.patchouli.common.network;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class NetworkHandler {

	public static void registerMessages() {
		ClientSidePacketRegistry.INSTANCE.register(MessageOpenBookGui.ID, MessageOpenBookGui::handle);
		ClientSidePacketRegistry.INSTANCE.register(MessageReloadBookContents.ID, MessageReloadBookContents::handle);
	}

}
