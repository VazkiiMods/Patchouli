package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PageLink extends PageText {

	String url;
	@SerializedName("link_text") IVariable linkText;

	transient Text realText;

	@Override
	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(entry, builder, pageNum);
		realText = linkText.as(Text.class);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		addButton(new ButtonWidget(GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, i18nText(realText.getString()), (b) -> GuiBook.openWebLink(url)));
	}

}
