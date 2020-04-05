package vazkii.patchouli.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.patchouli.client.book.ClientBookRegistry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MessageOpenBookGui {

	private final ResourceLocation book;
	@Nullable
	private final ResourceLocation entry;
	private final int page;

	public MessageOpenBookGui(PacketBuffer buf) {
		book = buf.readResourceLocation();
		String tmp = buf.readString();
		if (tmp.isEmpty()) {
			entry = null;
		} else {
			entry = ResourceLocation.tryCreate(tmp);
		}
		page = buf.readVarInt();
	}

	public MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry) {
		this(book, entry, 0);
	}
	
	public MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		this.book = book;
		this.entry = entry;
		this.page = page;
	}

	public void encode(PacketBuffer buf) {
		buf.writeResourceLocation(book);
		buf.writeString(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}
	
	public boolean receive(Supplier<Context> context) {
		if (context.get().getDirection().getReceptionSide().isClient()) {
			context.get().enqueueWork(() -> {
				ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
			});

			return true;
		}
		return false;
	}

}
