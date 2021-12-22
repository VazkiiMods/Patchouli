package vazkii.patchouli.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class BookContentsReloadEvent extends Event {
	private final ResourceLocation book;

	public BookContentsReloadEvent(ResourceLocation book) {
		this.book = book;
	}

	public ResourceLocation getBook() {
		return book;
	}
}
