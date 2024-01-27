package vazkii.patchouli.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.PacketDistributor;

import vazkii.patchouli.api.PatchouliAPI;

public record NeoForgeMessageReloadBookContents() implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "reload_books");

	public NeoForgeMessageReloadBookContents(final FriendlyByteBuf packetBuffer) {
		this();
	}

	public static void sendToAll(MinecraftServer server) {
		PacketDistributor.ALL.noArg().send(new NeoForgeMessageReloadBookContents());
	}

	public void write(FriendlyByteBuf buf) {}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
