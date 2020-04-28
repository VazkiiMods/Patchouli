package vazkii.patchouli.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public abstract class BookEvent
		extends Event {

	public final ResourceLocation book;

	public BookEvent(ResourceLocation book) {

		this.book = book;
	}
}
