package vazkii.patchouli.common.network.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;


import io.netty.buffer.Unpooled;

public class MessageOpenBookGui {
	public static final ResourceLocation ID = new ResourceLocation(Patchouli.MOD_ID, "open_book");

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
		ServerPlayNetworking.send(player, ID, buf);
	}

	public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		ResourceLocation book = buf.readResourceLocation();
		ResourceLocation entry;
		String tmp = buf.readUtf();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = ResourceLocation.tryParse(tmp);
		}

		int page = buf.readVarInt();
		client.submit(() -> ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page));
	}

}
