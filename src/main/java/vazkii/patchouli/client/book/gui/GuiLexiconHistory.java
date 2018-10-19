package vazkii.patchouli.client.book.gui;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookRegistry;

public class GuiLexiconHistory extends GuiLexiconEntryList {

	@Override
	protected String getName() {
		return I18n.translateToLocal("alquimia.gui.lexicon.history");
	}

	@Override
	protected String getDescriptionText() {
		return I18n.translateToLocal("alquimia.gui.lexicon.history.info");
	}
	
	@Override
	protected boolean shouldDrawProgressBar() {
		return false;
	}
	
	@Override
	protected boolean shouldSortEntryList() {
		return false;
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return PersistentData.data.history.stream()
				.map((s) -> new ResourceLocation(s))
				.map((res) -> BookRegistry.INSTANCE.entries.get(res))
				.filter((e) -> e != null && !e.isLocked())
				.collect(Collectors.toList());
	}

}
