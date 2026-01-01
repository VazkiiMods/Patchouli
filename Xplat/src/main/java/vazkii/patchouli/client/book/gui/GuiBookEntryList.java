package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class GuiBookEntryList extends GuiBook {

	public static final int ENTRIES_PER_PAGE = 13;
	public static final int ENTRIES_IN_FIRST_PAGE = 11;

	private @Nullable BookTextRenderer text;

	protected final List<Button> entryButtons = new ArrayList<>();
	private final List<BookEntry> allEntries = new ArrayList<>();
	private final List<BookEntry> visibleEntries = new ArrayList<>();

	private @Nullable EditBox searchField;

	public GuiBookEntryList(Book book, Component title) {
		super(book, title);
	}

	@Override
	public void init() {
		super.init();

		text = new BookTextRenderer(this, Component.literal(getDescriptionText()), LEFT_PAGE_X, TOP_PADDING + 22);

		allEntries.clear();
		allEntries.addAll(getEntries());
		allEntries.removeIf(BookEntry::shouldHide);
		if (shouldSortEntryList()) {
			Collections.sort(allEntries);
		}

		this.searchField = createSearchBar();
		buildEntryButtons();
	}

	protected EditBox createSearchBar() {
		EditBox field = new EditBox(font, 160, 170, 90, 12, Component.empty());
		field.setMaxLength(32);
		field.setBordered(false);
		field.setCanLoseFocus(false);
		field.setFocused(true);
		return field;
	}

	protected abstract String getDescriptionText();
	protected abstract Collection<BookEntry> getEntries();

	protected boolean doesEntryCountForProgress(BookEntry entry) {
		return true;
	}

	protected boolean shouldDrawProgressBar() {
		return true;
	}

	protected boolean shouldSortEntryList() {
		return true;
	}

	protected void addSubcategoryButtons() {
		// NO-OP
	}

	@Override
	void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);

		if (spread == 0) {
			drawCenteredStringNoShadow(graphics, getTitle().getVisualOrderText(), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawCenteredStringNoShadow(graphics, getChapterListTitle(), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

			drawSeparator(graphics, book, LEFT_PAGE_X, TOP_PADDING + 12);
			drawSeparator(graphics, book, RIGHT_PAGE_X, TOP_PADDING + 12);

			assert text != null;
			text.render(graphics, mouseX, mouseY, partialTicks);
			if (shouldDrawProgressBar()) {
				drawProgressBar(graphics, book, mouseX, mouseY, this::doesEntryCountForProgress);
			}
		} else if (spread % 2 == 1 && spread == maxSpreads - 1 && entryButtons.size() <= ENTRIES_PER_PAGE) {
			drawPageFiller(graphics, book);
		}

		assert searchField != null;
		if (!searchField.getValue().isEmpty()) {
			//RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			drawFromTexture(graphics, book, searchField.getX() - 8, searchField.getY(), 140, 183, 99, 14);
			Component toDraw = Component.literal(searchField.getValue()).setStyle(book.getFontStyle());
			graphics.drawString(font, toDraw, searchField.getX() + 7, searchField.getY() + 1, book.textColor, false);
		}

		if (visibleEntries.isEmpty()) {
			if (!searchField.getValue().isEmpty()) {
				drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.no_results"), GuiBook.RIGHT_PAGE_X + GuiBook.PAGE_WIDTH / 2, 80, 0x333333);
				graphics.pose().scale(2F, 2F);
				drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.sad"), GuiBook.RIGHT_PAGE_X / 2 + GuiBook.PAGE_WIDTH / 4, 47, 0x999999);
				graphics.pose().scale(0.5F, 0.5F);
			} else {
				drawCenteredStringNoShadow(graphics, getNoEntryMessage(), GuiBook.RIGHT_PAGE_X + GuiBook.PAGE_WIDTH / 2, 80, 0x333333);
			}
		}
	}

	protected String getChapterListTitle() {
		return I18n.get("patchouli.gui.lexicon.chapters");
	}

	protected String getNoEntryMessage() {
		return I18n.get("patchouli.gui.lexicon.no_entries");
	}

	@Override
	public boolean mouseClickedScaled(MouseButtonEvent event, boolean doubleClick) {
		assert text != null;
		assert searchField != null;
		return text.click(event, doubleClick)
				|| searchField.mouseClicked(new MouseButtonEvent(event.x() - bookLeft, event.y() - bookTop, event.buttonInfo()), doubleClick)
				|| super.mouseClickedScaled(event, doubleClick);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		assert searchField != null;
		String currQuery = searchField.getValue();
		if (searchField.charTyped(event)) {
			if (!searchField.getValue().equals(currQuery)) {
				buildEntryButtons();
			}

			return true;
		}

		return super.charTyped(event);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		assert searchField != null;
		String currQuery = searchField.getValue();

		if (event.key() == InputConstants.KEY_RETURN) {
			if (visibleEntries.size() == 1) {
				displayLexiconGui(new GuiBookEntry(book, visibleEntries.getFirst()), true);
				return true;
			}
		} else if (searchField.keyPressed(event)) {
			if (!searchField.getValue().equals(currQuery)) {
				buildEntryButtons();
			}

			return true;
		}

		return super.keyPressed(event);
	}

	public void handleButtonCategory(Button button) {
		displayLexiconGui(new GuiBookCategory(book, ((GuiButtonCategory) button).getCategory()), true);
	}

	public void handleButtonEntry(Button button) {
		GuiBookEntry.displayOrBookmark(this, ((GuiButtonEntry) button).getEntry());
	}

	@Override
	void onPageChanged() {
		buildEntryButtons();
	}

	void buildEntryButtons() {
		removeDrawablesIn(entryButtons);
		entryButtons.clear();
		visibleEntries.clear();

		assert searchField != null;
		String query = searchField.getValue().toLowerCase();
		allEntries.stream().filter((e) -> e.isFoundByQuery(query)).forEach(visibleEntries::add);

		maxSpreads = 1;
		int count = visibleEntries.size();
		count -= ENTRIES_IN_FIRST_PAGE;
		if (count > 0) {
			maxSpreads += (int) Math.ceil((float) count / (ENTRIES_PER_PAGE * 2));
		}

		while (getEntryCountStart() > visibleEntries.size()) {
			spread--;
		}

		if (spread == 0) {
			addEntryButtons(RIGHT_PAGE_X, TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
			addSubcategoryButtons();
		} else {
			int start = getEntryCountStart();
			addEntryButtons(LEFT_PAGE_X, TOP_PADDING, start, ENTRIES_PER_PAGE);
			addEntryButtons(RIGHT_PAGE_X, TOP_PADDING, start + ENTRIES_PER_PAGE, ENTRIES_PER_PAGE);
		}
	}

	int getEntryCountStart() {
		if (spread == 0) {
			return 0;
		}

		int start = ENTRIES_IN_FIRST_PAGE;
		start += (ENTRIES_PER_PAGE * 2) * (spread - 1);
		return start;
	}

	void addEntryButtons(int x, int y, int start, int count) {
		for (int i = 0; i < count && (i + start) < visibleEntries.size(); i++) {
			Button button = new GuiButtonEntry(this, bookLeft + x, bookTop + y + i * 11, visibleEntries.get(start + i), this::handleButtonEntry);
			addRenderableWidget(button);
			entryButtons.add(button);
		}
	}

	public String getSearchQuery() {
		return searchField == null ? "" : searchField.getValue();
	}

}
