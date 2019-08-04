/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [11/01/2016, 21:58:25 (GMT)]
 */
package vazkii.patchouli.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

public class NetworkHandler {
	
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Patchouli.MOD_ID, "main"))
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.simpleChannel();
	
	private static int i = 0;
	
	public static void registerMessages() {
		register(MessageSyncAdvancements.class, NetworkDirection.PLAY_TO_CLIENT);
		register(MessageOpenBookGui.class, NetworkDirection.PLAY_TO_CLIENT);
	}
	
	public static <T extends IMessage> void register(Class<T> clazz, NetworkDirection dir) {
		BiConsumer<T, PacketBuffer> encoder = (msg, buf) -> MessageSerializer.writeObject(msg, buf);
		
		Function<PacketBuffer, T> decoder = (buf) -> {
			try {
				T msg = clazz.newInstance();
				MessageSerializer.readObject(msg, buf);
				return msg;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			} 
		};
		
		BiConsumer<T, Supplier<NetworkEvent.Context>> consumer = (msg, supp) -> {
			NetworkEvent.Context context = supp.get();
			if(context.getDirection() != dir)
				return;
			
			context.setPacketHandled(msg.receive(context));
		};
		
		CHANNEL.registerMessage(i, clazz, encoder, decoder, consumer);
		i++;
	}

	public static void sendToPlayer(IMessage msg, ServerPlayerEntity player) {
		CHANNEL.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
	}
	
}
