package vazkii.patchouli.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

/**
 * This event is fired after a
 * book is reloaded.
 */
public interface BookContentsReloadCallback {
	Event<BookContentsReloadCallback> EVENT = EventFactory.createArrayBacked(BookContentsReloadCallback.class,
			(listeners) -> (b) -> {
				for (BookContentsReloadCallback l : listeners) {
					l.trigger(b);
				}
			});

	void trigger(Identifier book);
}
