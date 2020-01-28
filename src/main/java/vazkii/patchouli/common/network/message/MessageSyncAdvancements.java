package vazkii.patchouli.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.base.ClientAdvancements;

import java.util.function.Supplier;

public class MessageSyncAdvancements {

	public final String[] done;
	public final boolean showToast;
	
	public MessageSyncAdvancements(PacketBuffer buf) {
		int count = buf.readVarInt();
		done = new String[count];
		for (int i = 0; i < count; i++) {
			done[i] = buf.readString();
		}
		showToast = buf.readBoolean();
	}
	
	public MessageSyncAdvancements(String[] done, boolean showToast) { 
		this.done = done;
		this.showToast = showToast;
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(done.length);
		for (String s : done) {
			buf.writeString(s);
		}
		buf.writeBoolean(showToast);
	}
	
	public boolean receive(Supplier<NetworkEvent.Context> context) {
		if (context.get().getDirection().getReceptionSide().isClient()) {
			context.get().enqueueWork(() -> {
				ClientAdvancements.setDoneAdvancements(done, showToast, false);
			});
			return true;
		}

		return false;
	}
	
}
