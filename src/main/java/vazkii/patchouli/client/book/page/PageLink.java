package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiButton;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PageLink extends PageText {

	String url;
	@SerializedName("link_text")
	String linkText;

	transient GuiButton linkButton;

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		addButton(linkButton = new GuiButton(0, GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, linkText));
	}

	@Override
	protected void onButtonClicked(GuiButton button) {
		super.onButtonClicked(button);

		if(button == linkButton)
			GuiBook.openWebLink(url);
	}

}
