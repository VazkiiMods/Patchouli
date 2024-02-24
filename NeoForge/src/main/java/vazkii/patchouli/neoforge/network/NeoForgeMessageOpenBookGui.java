package vazkii.patchouli.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

public record NeoForgeMessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "open_book");

	public NeoForgeMessageOpenBookGui(FriendlyByteBuf buf) {
		this(buf.readResourceLocation(), getEntry(buf), buf.readVarInt());
	}

	private static ResourceLocation getEntry(FriendlyByteBuf buf) {
		String entry = buf.readUtf();
		return entry.isEmpty() ? null : new ResourceLocation(entry);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		player.connection.send(new NeoForgeMessageOpenBookGui(book, entry, page));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
