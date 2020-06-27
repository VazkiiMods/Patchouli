package vazkii.patchouli.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends Button {

	private Book book;

	public GuiButtonInventoryBook(Book book, int x, int y) {
		super(x, y, 20, 20, StringTextComponent.field_240750_d_, (b) -> {
			BookContents contents = book.contents;
			contents.openLexiconGui(contents.getCurrentGui(), false);
		});
		this.book = book;
	}

	@Override
	public void func_230431_b_(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(new ResourceLocation(Patchouli.MOD_ID, "textures/gui/inventory_button.png"));
		RenderSystem.color3f(1F, 1F, 1F);

		boolean hovered = mouseX >= field_230690_l_ && mouseY >= field_230691_m_ && mouseX < field_230690_l_ + field_230688_j_ && mouseY < field_230691_m_ + field_230689_k_;
		AbstractGui.func_238463_a_(ms, field_230690_l_, field_230691_m_, (hovered ? 20 : 0), 0, field_230688_j_, field_230689_k_, 64, 64);

		ItemStack stack = book.getBookItem();
		RenderHelper.renderItemStackInGui(ms, stack, field_230690_l_ + 2, field_230691_m_ + 2);

		EntryDisplayState readState = book.contents.getReadState();
		if (readState.hasIcon && readState.showInInventory) {
			GuiBook.drawMarking(ms, book, field_230690_l_, field_230691_m_, 0, readState);
		}
	}

	public Book getBook() {
		return book;
	}

}
