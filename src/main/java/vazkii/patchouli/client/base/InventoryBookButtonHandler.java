package vazkii.patchouli.client.base;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import vazkii.patchouli.client.gui.GuiButtonInventoryBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class InventoryBookButtonHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onGuiInitPost(InitGuiEvent.Post event) {
		String bookID = PatchouliConfig.inventoryButtonBook.get();
		Book book = BookRegistry.INSTANCE.books.get(new ResourceLocation(bookID));
		Screen gui = event.getGui();
		if (book == null || !(gui instanceof InventoryScreen)) {
			return;
		}

		List<Widget> buttons = event.getWidgetList();
		for (Widget button : buttons) {
			if (button instanceof ImageButton) {
				Button newButton = new GuiButtonInventoryBook(book, button.field_230690_l_, button.field_230691_m_ - 1);
				event.removeWidget(button);
				event.addWidget(newButton);
				return;
			}
		}
	}
}
