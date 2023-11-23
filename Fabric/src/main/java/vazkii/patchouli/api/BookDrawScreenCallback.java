package vazkii.patchouli.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

/**
 * This event is fired after any
 * book gui draws the content of a book with
 * the book gui scale still applied to the transformation state. This is useful if additional
 * custom components should be drawn independently of what page a book
 * is currently on.
 */
public interface BookDrawScreenCallback {
	Event<BookDrawScreenCallback> EVENT = EventFactory.createArrayBacked(BookDrawScreenCallback.class,
			(listeners) -> (b, g, mx, my, pt, gr) -> {
				for (BookDrawScreenCallback l : listeners) {
					l.trigger(b, g, mx, my, pt, gr);
				}
			});

	void trigger(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics);
}
