package vazkii.patchouli.client.book.page;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class PageEmpty extends BookPage {

	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		GuiLexicon.drawPageFiller(0, 0);
	}

}
