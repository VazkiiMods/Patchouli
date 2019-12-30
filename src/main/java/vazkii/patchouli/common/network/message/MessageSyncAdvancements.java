package vazkii.patchouli.common.network.message;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.common.base.Patchouli;

public class MessageSyncAdvancements {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "sync_advancements");

	public static void send(PlayerEntity player, String[] done, boolean showToast) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(done.length);
		for (String s : done) {
			buf.writeString(s);
		}
		buf.writeBoolean(showToast);

		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ID, buf);
	}

	public static void handle(PacketContext ctx, PacketByteBuf buf) {
		String[] done = new String[buf.readVarInt()];
		for (int i = 0; i < done.length; i++) {
			done[i] = buf.readString();
		}
		boolean showToast = buf.readBoolean();
		ctx.getTaskQueue().submit(() -> {
			ClientAdvancements.setDoneAdvancements(done, showToast, false);
		});
	}

}
