package vazkii.patchouli.common.network.message;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

public class MessageOpenBookGui {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "open_book");

	public static void send(PlayerEntity player, Identifier book) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(book);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
	}
	
	public static void handle(PacketContext context, PacketByteBuf buf) {
		Identifier book = buf.readIdentifier();
		context.getTaskQueue().submit(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(book);
		});
	}

}
