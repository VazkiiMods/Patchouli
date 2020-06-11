package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageEmpty extends BookPage {

	@SerializedName("draw_filler") boolean filler = true;

	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		if (filler) {
			GuiBook.drawPageFiller(book, 0, 0);
		}
	}

}
