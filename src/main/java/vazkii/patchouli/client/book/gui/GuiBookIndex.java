package vazkii.patchouli.client.book.gui;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.Collection;

public class GuiBookIndex extends GuiBookEntryList {

	public GuiBookIndex(Book book) {
		super(book, new TranslatableComponent("patchouli.gui.lexicon.index"));
	}

	@Override
	protected String getDescriptionText() {
		return I18n.get("patchouli.gui.lexicon.index.info");
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return book.getContents().entries.values();
	}

}
