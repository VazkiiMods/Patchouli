package vazkii.patchouli.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;

import javax.annotation.Nullable;

import java.util.function.Supplier;

public record MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) {
	public static final ResourceLocation ID = new ResourceLocation(Patchouli.MOD_ID, "open_book");

	public MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		this.book = book;
		this.entry = entry;
		this.page = page;
	}

	public static MessageOpenBookGui decode(FriendlyByteBuf buf) {
		ResourceLocation book = buf.readResourceLocation();
		ResourceLocation entry;
		String tmp = buf.readUtf();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = ResourceLocation.tryParse(tmp);
		}

		int page = buf.readVarInt();
		return new MessageOpenBookGui(book, entry, page);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageOpenBookGui(book, entry, page));
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page));
		ctx.get().setPacketHandled(true);
	}

}
