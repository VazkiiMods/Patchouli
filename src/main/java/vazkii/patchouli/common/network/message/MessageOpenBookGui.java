package vazkii.patchouli.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.patchouli.client.book.ClientBookRegistry;

import java.util.function.Supplier;

public class MessageOpenBookGui {

	public final String book;
	
	public MessageOpenBookGui(PacketBuffer buf) {
		book = buf.readString();
	}
	
	public MessageOpenBookGui(String book) { 
		this.book = book;
	}

	public void encode(PacketBuffer buf) {
		buf.writeString(book);
	}
	
	public boolean receive(Supplier<Context> context) {
		if (context.get().getDirection().getReceptionSide().isClient()) {
			context.get().enqueueWork(() -> {
				ClientBookRegistry.INSTANCE.displayBookGui(book);
			});

			return true;
		}
		return false;
	}

}
