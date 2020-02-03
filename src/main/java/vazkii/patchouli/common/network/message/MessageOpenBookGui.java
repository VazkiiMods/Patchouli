package vazkii.patchouli.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.patchouli.client.book.ClientBookRegistry;

import java.util.function.Supplier;

public class MessageOpenBookGui {

	public final ResourceLocation book;
	
	public MessageOpenBookGui(PacketBuffer buf) {
		book = buf.readResourceLocation();
	}
	
	public MessageOpenBookGui(ResourceLocation book) {
		this.book = book;
	}

	public void encode(PacketBuffer buf) {
		buf.writeResourceLocation(book);
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
