package vazkii.patchouli.client.base;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.gui.GuiButtonInventoryBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class InventoryBookButtonHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onGuiInitPost(InitGuiEvent.Post event) {
		String bookID = PatchouliConfig.inventoryButtonBook;
		Book book = BookRegistry.INSTANCE.books.get(new ResourceLocation(bookID));
		if(book == null || !(event.getGui() instanceof GuiInventory))
			return;

		List<GuiButton> buttons = event.getButtonList();

		for(int i = 0; i < buttons.size(); i++) {
			GuiButton button = buttons.get(i);
			if(button.id == 10) {
				GuiButton newButton = new GuiButtonInventoryBook(book, button.id, button.x, button.y - 1);
				buttons.set(i, newButton);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void onActionPressed(ActionPerformedEvent.Pre event) {
		GuiButton button = event.getButton();
		if(button instanceof GuiButtonInventoryBook) {
			GuiButtonInventoryBook bookButton = (GuiButtonInventoryBook) button;
			BookContents contents = bookButton.getBook().contents;
			contents.openLexiconGui(contents.getCurrentGui(), false);
			button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
			event.setCanceled(true);
		}
	}

}
