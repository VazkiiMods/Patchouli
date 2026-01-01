package vazkii.patchouli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import vazkii.patchouli.api.PatchouliAPI;

public record MessageReloadBookContents() implements CustomPacketPayload {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "reload_books");
	public static final StreamCodec<FriendlyByteBuf, MessageReloadBookContents> CODEC = StreamCodec.unit(new MessageReloadBookContents());
	public static final Type<MessageReloadBookContents> TYPE = new Type<>(ID);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
