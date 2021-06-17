package vazkii.patchouli.client.book.gui;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.Collection;

public class GuiBookIndex extends GuiBookEntryList {

	public GuiBookIndex(Book book) {
		super(book, new TranslatableText("patchouli.gui.lexicon.index"));
	}

	@Override
	protected String getDescriptionText() {
		return I18n.translate("patchouli.gui.lexicon.index.info");
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return book.getContents().entries.values();
	}

}
