package vazkii.patchouli.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} after a
 * book is reloaded.
 */
public class BookContentsReloadEvent
    extends Event {

  public final ResourceLocation book;

  public BookContentsReloadEvent(ResourceLocation book) {

    this.book = book;
  }
}
