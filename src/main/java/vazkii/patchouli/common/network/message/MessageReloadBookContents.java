package vazkii.patchouli.common.network.message;

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;


import io.netty.buffer.Unpooled;

public class MessageReloadBookContents {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "reload_books");

	public static void sendToAll(MinecraftServer server) {
		PlayerStream.all(server).forEach(MessageReloadBookContents::send);
	}

	public static void send(PlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
	}

	public static void handle(PacketContext context, PacketByteBuf buf) {
		context.getTaskQueue().submit(ClientBookRegistry.INSTANCE::reload);
	}
}
