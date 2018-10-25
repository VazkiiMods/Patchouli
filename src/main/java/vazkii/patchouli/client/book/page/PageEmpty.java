package vazkii.patchouli.client.book.page;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageEmpty extends BookPage {

	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		GuiBook.drawPageFiller(book, 0, 0);
	}

}
