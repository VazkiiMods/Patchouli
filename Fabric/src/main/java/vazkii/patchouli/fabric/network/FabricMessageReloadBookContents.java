package vazkii.patchouli.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.ClientBookRegistry;


import io.netty.buffer.Unpooled;

public class FabricMessageReloadBookContents {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "reload_books");

	public static void sendToAll(MinecraftServer server) {
		PlayerLookup.all(server).forEach(FabricMessageReloadBookContents::send);
	}

	public static void send(ServerPlayer player) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);
		ServerPlayNetworking.send(player, ID, buf);
	}

	public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		client.submit(() -> ClientBookRegistry.INSTANCE.reload(false));
	}
}
