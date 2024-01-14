package vazkii.patchouli.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

public record ForgeMessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "open_book");

	public ForgeMessageOpenBookGui(FriendlyByteBuf buf) {
		this(buf.readResourceLocation(), ResourceLocation.tryParse(buf.readUtf()), buf.readVarInt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		player.connection.send(new ForgeMessageOpenBookGui(book, entry, page));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
