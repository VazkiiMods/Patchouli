package vazkii.patchouli.common.network.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;


import io.netty.buffer.Unpooled;

public class MessageOpenBookGui {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "open_book");

	public static void send(ServerPlayerEntity player, Identifier book, @Nullable Identifier entry, int page) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(book);
		buf.writeString(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
		ServerPlayNetworking.send(player, ID, buf);
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		Identifier book = buf.readIdentifier();
		Identifier entry;
		String tmp = buf.readString();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = Identifier.tryParse(tmp);
		}

		int page = buf.readVarInt();
		client.submit(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
		});
	}

}
