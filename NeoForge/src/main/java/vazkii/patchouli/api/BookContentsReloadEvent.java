package vazkii.patchouli.api;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;

public class BookContentsReloadEvent extends Event {
	private final Identifier book;

	public BookContentsReloadEvent(Identifier book) {
		this.book = book;
	}

	public Identifier getBook() {
		return book;
	}
}
