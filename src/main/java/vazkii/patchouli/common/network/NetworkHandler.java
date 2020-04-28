/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 * File Created @ [11/01/2016, 21:58:25 (GMT)]
 */
package vazkii.patchouli.common.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
		CHANNEL.messageBuilder(MessageOpenBookGui.class, 1)
				.encoder(MessageOpenBookGui::encode)
				.decoder(MessageOpenBookGui::new)
				.consumer(MessageOpenBookGui::receive)
				.add();
		CHANNEL.messageBuilder(MessageReloadBookContents.class, 2)
				.encoder(MessageReloadBookContents::encode)
				.decoder(MessageReloadBookContents::new)
				.consumer(MessageReloadBookContents::receive)
				.add();
	}

	public static void sendToPlayer(Object msg, ServerPlayerEntity player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendToAll(Object msg) {
		CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
	}

}
