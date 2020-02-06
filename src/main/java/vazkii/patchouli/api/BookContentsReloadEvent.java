package vazkii.patchouli.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} after a
 * book is reloaded.
 */
public class BookContentsReloadEvent
    extends BookEvent {

  public BookContentsReloadEvent(ResourceLocation book) {
    super(book);
  }
}
