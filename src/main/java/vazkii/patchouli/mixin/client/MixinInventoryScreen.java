package vazkii.patchouli.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.gui.GuiButtonInventoryBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> {
	public MixinInventoryScreen(InventoryMenu container, Inventory playerInventory, Component text) {
		super(container, playerInventory, text);
	}

	@Inject(at = @At("RETURN"), method = "init()V")
	public void onGuiInitPost(CallbackInfo info) {
		String bookID = PatchouliConfig.inventoryButtonBook.get();
		Book book = BookRegistry.INSTANCE.books.get(new ResourceLocation(bookID));
		if (book == null) {
			return;
		}

		Widget replaced = null;
		Button replacement = null;
		for (int i = 0; i < this.renderables.size(); i++) {
			Widget button = this.renderables.get(i);
			if (button instanceof ImageButton tex) {
				replaced = button;
				replacement = new GuiButtonInventoryBook(book, tex.x, tex.y - 1);
				this.renderables.set(i, replacement);
				break;
			}
		}

		int i = ((AccessorScreen) this).getChildren().indexOf(replaced);
		if (i >= 0) {
			((AccessorScreen) this).getChildren().set(i, replacement);
		}

		i = ((AccessorScreen) this).getNarratables().indexOf(replaced);
		if (i >= 0) {
			((AccessorScreen) this).getNarratables().set(i, replacement);
		}
	}
}
