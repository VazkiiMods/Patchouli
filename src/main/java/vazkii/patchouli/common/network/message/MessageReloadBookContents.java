package vazkii.patchouli.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.*;
import java.util.function.Supplier;

public record MessageReloadBookContents(Collection<Book> books) {

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(books.size());
		books.forEach((book) -> {
			buf.writeResourceLocation(book.getId());
			buf.writeUtf(BookRegistry.GSON.toJson(book));
		});
	}

	public static MessageReloadBookContents decode(FriendlyByteBuf buf) {
		List<Book> books = new ArrayList<>();
		for (int i = buf.readInt(); i > 0; i--) {
			ResourceLocation id = buf.readResourceLocation();
			Book book = BookRegistry.GSON.fromJson(buf.readUtf(), Book.class);
			book.build(id);
			books.add(book);
		}
		return new MessageReloadBookContents(books);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		context.enqueueWork(() -> {
			BookRegistry.INSTANCE.sync(books);
			ClientBookRegistry.INSTANCE.reload();
			ClientBookRegistry.INSTANCE.refreshModels();
			ClientBookRegistry.INSTANCE.reloadResources();
		});
		context.setPacketHandled(true);
	}
}
