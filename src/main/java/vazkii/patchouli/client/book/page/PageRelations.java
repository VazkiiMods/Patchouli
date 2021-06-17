package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageRelations extends PageWithText {

	List<String> entries;
	String title;

	transient List<BookEntry> entryObjs;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);

		entryObjs = entries.stream()
				.map(Identifier::new)
				.map(book.getContents().entries::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		List<BookEntry> displayedEntries = new ArrayList<>(entryObjs);
		displayedEntries.removeIf(BookEntry::shouldHide);
		Collections.sort(displayedEntries);
		for (int i = 0; i < displayedEntries.size(); i++) {
			ButtonWidget button = new GuiButtonEntry(parent, 0, 20 + i * 11, displayedEntries.get(i), this::handleButtonEntry);
			addButton(button);
		}
	}

	public void handleButtonEntry(ButtonWidget button) {
		GuiBookEntry.displayOrBookmark(parent, ((GuiButtonEntry) button).getEntry());
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		parent.drawCenteredStringNoShadow(ms, title == null || title.isEmpty() ? I18n.translate("patchouli.gui.lexicon.relations") : i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		GuiBook.drawSeparator(ms, book, 0, 12);

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 22 + entryObjs.size() * 11;
	}

}
