package vazkii.patchouli.mixin.client;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.gui.GuiButtonInventoryBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> {
	public MixinInventoryScreen(PlayerScreenHandler container, PlayerInventory playerInventory, Text text) {
		super(container, playerInventory, text);
	}

	@Inject(at = @At("RETURN"), method = "init()V")
	public void onGuiInitPost(CallbackInfo info) {
		String bookID = PatchouliConfig.inventoryButtonBook.getValue();
		Book book = BookRegistry.INSTANCE.books.get(new Identifier(bookID));
		if (book == null) {
			return;
		}

		Drawable replaced = null;
		ButtonWidget replacement = null;
		for (int i = 0; i < ((AccessorScreen) this).getDrawables().size(); i++) {
			Drawable button = ((AccessorScreen) this).getDrawables().get(i);
			if (button instanceof TexturedButtonWidget tex) {
				replaced = button;
				replacement = new GuiButtonInventoryBook(book, tex.x, tex.y - 1);
				((AccessorScreen) this).getDrawables().set(i, replacement);
				break;
			}
		}

		int i = ((AccessorScreen) this).getChildren().indexOf(replaced);
		if (i >= 0) {
			((AccessorScreen) this).getChildren().set(i, replacement);
		}
	}
}
