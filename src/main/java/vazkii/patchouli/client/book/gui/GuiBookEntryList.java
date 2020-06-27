package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import org.lwjgl.glfw.GLFW;

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

	BookTextRenderer text;

	List<Button> dependentButtons;
	List<BookEntry> allEntries;
	List<BookEntry> visibleEntries;

	TextFieldWidget searchField;

	public GuiBookEntryList(Book book, ITextComponent title) {
		super(book, title);
	}

	@Override
	public void func_231160_c_() {
		super.func_231160_c_();

		text = new BookTextRenderer(this, getDescriptionText(), LEFT_PAGE_X, TOP_PADDING + 22);

		visibleEntries = new ArrayList<>();
		allEntries = new ArrayList<>(getEntries());
		allEntries.removeIf(BookEntry::shouldHide);
		if (shouldSortEntryList()) {
			Collections.sort(allEntries);
		}

		searchField = new TextFieldWidget(field_230712_o_, 160, 170, 90, 12, StringTextComponent.field_240750_d_);
		searchField.setMaxStringLength(32);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setCanLoseFocus(false);
		searchField.setFocused2(true);

		dependentButtons = new ArrayList<>();
		buildEntryButtons();
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
	void drawForegroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(ms, mouseX, mouseY, partialTicks);

		if (spread == 0) {
			drawCenteredStringNoShadow(ms, func_231171_q_(), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawCenteredStringNoShadow(ms, I18n.format("patchouli.gui.lexicon.chapters"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

			drawSeparator(ms, book, LEFT_PAGE_X, TOP_PADDING + 12);
			drawSeparator(ms, book, RIGHT_PAGE_X, TOP_PADDING + 12);

			text.render(ms, mouseX, mouseY);
			if (shouldDrawProgressBar()) {
				drawProgressBar(ms, book, mouseX, mouseY, this::doesEntryCountForProgress);
			}
		} else if (spread % 2 == 1 && spread == maxSpreads - 1 && dependentButtons.size() <= ENTRIES_PER_PAGE) {
			drawPageFiller(ms, book);
		}

		if (!searchField.getText().isEmpty()) {
			RenderSystem.color4f(1F, 1F, 1F, 1F);
			drawFromTexture(ms, book, searchField.field_230690_l_ - 8, searchField.field_230691_m_, 140, 183, 99, 14);
			ITextComponent toDraw = new StringTextComponent(searchField.getText()).func_230530_a_(book.getFontStyle());
			field_230712_o_.func_238422_b_(ms, toDraw, searchField.field_230690_l_ + 7, searchField.field_230691_m_ + 1, 0);
		}

		if (visibleEntries.isEmpty()) {
			if (!searchField.getText().isEmpty()) {
				drawCenteredStringNoShadow(ms, I18n.format("patchouli.gui.lexicon.no_results"), GuiBook.RIGHT_PAGE_X + GuiBook.PAGE_WIDTH / 2, 80, 0x333333);
				ms.scale(2F, 2F, 2F);
				drawCenteredStringNoShadow(ms, I18n.format("patchouli.gui.lexicon.sad"), GuiBook.RIGHT_PAGE_X / 2 + GuiBook.PAGE_WIDTH / 4, 47, 0x999999);
				ms.scale(0.5F, 0.5F, 0.5F);
			} else {
				drawCenteredStringNoShadow(ms, I18n.format("patchouli.gui.lexicon.no_entries"), GuiBook.RIGHT_PAGE_X + GuiBook.PAGE_WIDTH / 2, 80, 0x333333);
			}
		}
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		return text.click(mouseX, mouseY, mouseButton)
				|| searchField.func_231044_a_(mouseX - bookLeft, mouseY - bookTop, mouseButton)
				|| super.mouseClickedScaled(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean func_231042_a_(char c, int i) {
		String currQuery = searchField.getText();
		if (searchField.func_231042_a_(c, i)) {
			if (!searchField.getText().equals(currQuery)) {
				buildEntryButtons();
			}

			return true;
		}

		return super.func_231042_a_(c, i);
	}

	@Override
	public boolean func_231046_a_(int key, int scanCode, int modifiers) {
		String currQuery = searchField.getText();

		if (key == GLFW.GLFW_KEY_ENTER) {
			if (visibleEntries.size() == 1) {
				displayLexiconGui(new GuiBookEntry(book, visibleEntries.get(0)), true);
				return true;
			}
		} else if (searchField.func_231046_a_(key, scanCode, modifiers)) {
			if (!searchField.getText().equals(currQuery)) {
				buildEntryButtons();
			}

			return true;
		}

		return super.func_231046_a_(key, scanCode, modifiers);
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
		removeButtonsIn(dependentButtons);
		dependentButtons.clear();
		visibleEntries.clear();

		String query = searchField.getText().toLowerCase();
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
			func_230480_a_(button);
			dependentButtons.add(button);
		}
	}

	public String getSearchQuery() {
		return searchField.getText();
	}

}
