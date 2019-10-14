package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.widget.button.Button;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PageLink extends PageText {

	String url;
	@SerializedName("link_text")
	String linkText;

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		addButton(new Button(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, i18n(linkText), (b) -> GuiBook.openWebLink(url)));
	}

}
