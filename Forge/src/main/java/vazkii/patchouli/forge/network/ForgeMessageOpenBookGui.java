package vazkii.patchouli.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import vazkii.patchouli.client.book.ClientBookRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ForgeMessageOpenBookGui {
	private final ResourceLocation book;
	@Nullable private final ResourceLocation entry;
	private final int page;

	public ForgeMessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		this.book = book;
		this.entry = entry;
		this.page = page;
	}

	public static ForgeMessageOpenBookGui decode(FriendlyByteBuf buf) {
		ResourceLocation book = buf.readResourceLocation();
		ResourceLocation entry;
		String tmp = buf.readUtf();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = ResourceLocation.tryParse(tmp);
		}

		int page = buf.readVarInt();
		return new ForgeMessageOpenBookGui(book, entry, page);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		ForgeNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ForgeMessageOpenBookGui(book, entry, page));
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page));
		ctx.get().setPacketHandled(true);
	}
}
