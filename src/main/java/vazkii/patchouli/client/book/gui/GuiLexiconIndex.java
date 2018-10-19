package vazkii.patchouli.client.book.gui;

import java.util.Collection;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookRegistry;

public class GuiLexiconIndex extends GuiLexiconEntryList {

	@Override
	protected String getName() {
		return I18n.translateToLocal("alquimia.gui.lexicon.index");
	}

	@Override
	protected String getDescriptionText() {
		return I18n.translateToLocal("alquimia.gui.lexicon.index.info");
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return BookRegistry.INSTANCE.entries.values();
	}

}
