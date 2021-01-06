package vazkii.patchouli.client.book.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

public class GuiBookCategory extends GuiBookEntryList {

	final BookCategory category;
	private int subcategoryButtonCount;
	
	public GuiBookCategory(Book book, BookCategory category) {
		super(book);
		this.category = category;
	}

	@Override
	protected String getName() {
		return category.getName();
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
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(mouseX, mouseY, partialTicks);
		if (getEntries().isEmpty() && subcategoryButtonCount <= 16 && subcategoryButtonCount > 0) {
			int bottomSeparator = TOP_PADDING + 37 + 24 * ((subcategoryButtonCount - 1) / 4 + 1);
			drawSeparator(book, RIGHT_PAGE_X, bottomSeparator);
		}
	}
	
	@Override
	protected void addSubcategoryButtons() {
		int i = 0;
		List<BookCategory> categories = new ArrayList<>(book.contents.categories.values());
		categories.removeIf(cat -> cat.getParentCategory() != category || cat.shouldHide());
		Collections.sort(categories);
		subcategoryButtonCount = categories.size();

		int baseX, baseY;
		boolean rightPageFree = getEntries().isEmpty();
		if (rightPageFree) {
			baseX = RIGHT_PAGE_X + 10;
			baseY = TOP_PADDING + 25;
		} else {
			baseX = LEFT_PAGE_X + 10;
			baseY = TOP_PADDING + PAGE_HEIGHT - (categories.size() / 4) * 20 - (PatchouliConfig.disableAdvancementLocking ? 38 : 64);
		}

		for (BookCategory ocategory : categories) {
			int x = baseX + (i % 4) * 24;
			int y = baseY + (i / 4) * (rightPageFree ? 24 : 20);
			
			GuiButton button = new GuiButtonCategory(this, x, y, ocategory);
			buttonList.add(button);
			dependentButtons.add(button);
			
			i++;
		}
	}

	@Override
	protected String getChapterListTitle() {
		if (getEntries().isEmpty() && subcategoryButtonCount > 0) {
			return I18n.format("patchouli.gui.lexicon.categories");
		}
		return super.getChapterListTitle();
	}

	@Override
	protected GuiTextField createSearchBar() {
		GuiTextField widget = super.createSearchBar();
		if (getEntries().isEmpty()) {
			widget.setEnabled(false);
			widget.setVisible(false);
		}
		return widget;
	}
	
	@Override
	protected boolean doesEntryCountForProgress(BookEntry entry) {
		return entry.getCategory() == category;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof GuiBookCategory && ((GuiBookCategory) obj).category == category && ((GuiBookCategory) obj).page == page);
	}
	
	@Override
	public boolean canBeOpened() {
		return !category.isLocked() && !equals(Minecraft.getMinecraft().currentScreen);
	}
	
}
