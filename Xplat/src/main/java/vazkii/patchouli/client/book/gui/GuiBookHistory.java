package vazkii.patchouli.client.book.gui;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.BookData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

import java.util.Collection;
import java.util.stream.Collectors;

public class GuiBookHistory extends GuiBookEntryList {

	public GuiBookHistory(Book book) {
		super(book, new TranslatableComponent("patchouli.gui.lexicon.history"));
	}

	@Override
	protected String getDescriptionText() {
		return I18n.get("patchouli.gui.lexicon.history.info");
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
		BookData data = PersistentData.data.getBookData(book);

		return data.history.stream()
				.map(ResourceLocation::new)
				.map((res) -> book.getContents().entries.get(res))
				.filter((e) -> e != null && !e.isLocked())
				.collect(Collectors.toList());
	}

}
