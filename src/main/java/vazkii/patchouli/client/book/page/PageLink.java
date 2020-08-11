package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PageLink extends PageText {

	String url;
	@SerializedName("link_text") IVariable linkText;

	transient ITextComponent realText;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		realText = linkText.as(ITextComponent.class);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		addButton(new Button(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, i18nText(realText.getString()), (b) -> GuiBook.openWebLink(url)));
	}

}
