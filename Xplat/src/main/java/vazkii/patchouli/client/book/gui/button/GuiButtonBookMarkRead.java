package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonBookMarkRead extends GuiButtonBook {

	private final Book book;

	public GuiButtonBookMarkRead(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 31, 11, 11, Button::onPress, getTooltip(parent.book));
		this.book = parent.book;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int px = getX() + 1;
		int py = (int) (getY() + 0.5);
		GuiBook.drawFromTexture(graphics, book, getX(), getY(), 285, 160, 13, 10);
		GuiBook.drawFromTexture(graphics, book, px, py, u, v, width, height);
		if (isHoveredOrFocused()) {
			GuiBook.drawFromTexture(graphics, book, px, py, u + 11, v, width, height);
			parent.setTooltip(getTooltipLines());
		}
		graphics.drawString(parent.getMinecraft().font, "+", px, py, 0x00FF01, true);
	}

	@Override
	public void onPress() {
		for (BookEntry entry : this.book.getContents().entries.values()) {
			if (isMainPage(this.book)) {
				markEntry(entry);
			} else {
				markCategoryAsRead(entry, entry.getCategory(), this.book.getContents().entries.size());
			}
		}
	}

	private void markCategoryAsRead(BookEntry entry, BookCategory category, int maxRecursion) {
		if (category.getName().equals(this.book.getContents().getCurrentGui().getTitle())) {
			markEntry(entry);
		} else if (!category.isRootCategory() && maxRecursion > 0) {
			markCategoryAsRead(entry, entry.getCategory().getParentCategory(), maxRecursion - 1);
		}
	}

	private void markEntry(BookEntry entry) {
		boolean dirty = false;
		var key = entry.getId();

		if (!entry.isLocked() && entry.getReadState().equals(EntryDisplayState.UNREAD)) {
			PersistentData.BookData data = PersistentData.data.getBookData(book);

			if (!data.viewedEntries.contains(key)) {
				data.viewedEntries.add(key);
				dirty = true;
				entry.markReadStateDirty();
			}
		}

		if (dirty) {
			PersistentData.save();
		}
	}

	private static Component getTooltip(Book book) {
		String text = isMainPage(book) ? "patchouli.gui.lexicon.button.mark_all_read" : "patchouli.gui.lexicon.button.mark_category_read";
		return Component.translatable(text);
	}

	private static boolean isMainPage(Book book) {
		return !book.getContents().currentGui.canSeeBackButton();
	}
}
