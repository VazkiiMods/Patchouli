package vazkii.patchouli.common.network.message;

import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;


import io.netty.buffer.Unpooled;

public class MessageOpenBookGui {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "open_book");

	public static void send(PlayerEntity player, Identifier book, @Nullable Identifier entry, int page) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(book);
		buf.writeString(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
	}

	public static void handle(PacketContext context, PacketByteBuf buf) {
		Identifier book = buf.readIdentifier();
		Identifier entry;
		String tmp = buf.readString();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = Identifier.tryParse(tmp);
		}

		int page = buf.readVarInt();
		context.getTaskQueue().submit(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
		});
	}

}
