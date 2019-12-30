package vazkii.patchouli.client.book.gui;

import java.util.Collection;

import net.minecraft.client.resource.language.I18n;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

public class GuiBookIndex extends GuiBookEntryList {

	public GuiBookIndex(Book book) {
		super(book);
	}

	@Override
	protected String getName() {
		return I18n.translate("patchouli.gui.lexicon.index");
	}

	@Override
	protected String getDescriptionText() {
		return I18n.translate("patchouli.gui.lexicon.index.info");
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return book.contents.entries.values();
	}

}
