package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiButton;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageLink extends PageWithText {

	String url;
	String link_text;

	transient GuiButton linkButton;

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		adddButton(linkButton = new GuiButton(0, GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, link_text));
	}
	
	@Override
	public int getTextHeight() {
		return 0;
	}

	@Override
	protected void onButtonClicked(GuiButton button) {
		super.onButtonClicked(button);

		if(button == linkButton)
			GuiBook.openWebLink(url);
	}

}
