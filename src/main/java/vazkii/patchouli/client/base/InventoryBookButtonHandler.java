package vazkii.patchouli.client.base;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.gui.GuiButtonInventoryBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@EventBusSubscriber(Dist.CLIENT)
public class InventoryBookButtonHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onGuiInitPost(InitGuiEvent.Post event) {
		String bookID = PatchouliConfig.inventoryButtonBook.get();
		Book book = BookRegistry.INSTANCE.books.get(new ResourceLocation(bookID));
		Screen gui = event.getGui();
		if(book == null || !(gui instanceof InventoryScreen))
			return;

		List<Widget> buttons = event.getWidgetList();
		for (Widget button : buttons) {
			if (button instanceof ImageButton) {
				Button newButton = new GuiButtonInventoryBook(book, button.x, button.y - 1);
				event.removeWidget(button);
				event.addWidget(newButton);
				return;
			}
		}
	}
}
