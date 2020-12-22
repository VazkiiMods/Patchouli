package vazkii.patchouli.client.book.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.common.book.Book;

import java.util.*;

public class GuiBookCategory extends GuiBookEntryList {

	BookCategory category;

	public GuiBookCategory(Book book, BookCategory category) {
		super(book, category.getName());
		this.category = category;
	}

	@Override
	protected String getDescriptionText() {
		return category.getDescription();
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return category.getEntries();
	}

	@Override
	protected void addSubcategoryButtons() {
		int i = 0;
		List<BookCategory> categories = new ArrayList<>(book.contents.categories.values());
		categories.removeIf(cat -> cat.getParentCategory() != category || cat.shouldHide());
		Collections.sort(categories);

		int baseY = TOP_PADDING + PAGE_HEIGHT - (categories.size() / 4) * 20 - (!book.advancementsEnabled() ? 46 : 68);

		for (BookCategory ocategory : categories) {
			int x = LEFT_PAGE_X + 10 + (i % 4) * 24;
			int y = baseY + (i / 4) * 20;

			Button button = new GuiButtonCategory(this, x, y, ocategory, this::handleButtonCategory);
			addButton(button);
			dependentButtons.add(button);

			i++;
		}
	}

	@Override
	protected boolean doesEntryCountForProgress(BookEntry entry) {
		return entry.getCategory() == category;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof GuiBookCategory && ((GuiBookCategory) obj).category == category && ((GuiBookCategory) obj).spread == spread);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(category) * 31 + Objects.hashCode(spread);
	}

	@Override
	public boolean canBeOpened() {
		return !category.isLocked() && !equals(Minecraft.getInstance().currentScreen);
	}

}
