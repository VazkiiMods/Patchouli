package vazkii.patchouli.client.book.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.Collection;

public class GuiBookIndex extends GuiBookEntryList {

	public GuiBookIndex(Book book) {
		super(book, new TranslationTextComponent("patchouli.gui.lexicon.index"));
	}

	@Override
	protected String getDescriptionText() {
		return I18n.format("patchouli.gui.lexicon.index.info");
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return book.contents.entries.values();
	}

}
