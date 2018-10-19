package vazkii.patchouli.client.book.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

public abstract class GuiLexiconEntryList extends GuiLexicon {

	public static final int ENTRIES_PER_PAGE = 13;
	public static final int ENTRIES_IN_FIRST_PAGE = 11;
	
	LexiconTextRenderer text;
	
	List<GuiButton> dependentButtons;
	List<BookEntry> allEntries;
	List<BookEntry> visibleEntries;
	
	GuiTextField searchField;

	@Override
	public void initGui() {
		super.initGui();
		
		text = new LexiconTextRenderer(this, getDescriptionText(), LEFT_PAGE_X, TOP_PADDING + 22);
		
		visibleEntries = new ArrayList<>();
		allEntries = new ArrayList<>(getEntries());
		allEntries.removeIf(BookEntry::shouldHide);
		if(shouldSortEntryList())
			Collections.sort(allEntries);
		
		searchField = new GuiTextField(0, fontRenderer, 160, 170, 90, 12);
		searchField.setMaxStringLength(32);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setCanLoseFocus(false);
		searchField.setFocused(true);
		
		dependentButtons = new ArrayList();
		buildEntryButtons();
	}
	
	protected abstract String getName();
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
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(mouseX, mouseY, partialTicks);
		
		if(page == 0) {
			drawCenteredStringNoShadow(getName(), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, 0x333333);
			drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.chapters"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, 0x333333);

			drawSeparator(LEFT_PAGE_X, TOP_PADDING + 12);
			drawSeparator(RIGHT_PAGE_X, TOP_PADDING + 12);

			text.render(mouseX, mouseY);
			if(shouldDrawProgressBar())
				drawProgressBar(mouseX, mouseY, this::doesEntryCountForProgress);
		} else if(page % 2 == 1 && page == maxpages - 1)
			drawPageFiller();
		
		if(!searchField.getText().isEmpty()) {
			GlStateManager.color(1F, 1F, 1F, 1F);
			drawFromTexture(searchField.x - 8, searchField.y, 140, 183, 99, 14);
			boolean unicode = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(true);
			fontRenderer.drawString(searchField.getText(), searchField.x + 7, searchField.y + 1, 0);
			fontRenderer.setUnicodeFlag(unicode);
		}
		
		if(visibleEntries.isEmpty()) {
			drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.no_results"), GuiLexicon.RIGHT_PAGE_X + GuiLexicon.PAGE_WIDTH / 2, 80, 0x333333);
			GlStateManager.scale(2F, 2F, 2F);
			drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.sad"), GuiLexicon.RIGHT_PAGE_X / 2 + GuiLexicon.PAGE_WIDTH / 4, 47, 0x999999);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		text.click(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX - bookLeft, mouseY - bookTop, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 28) { // Enter
			if(visibleEntries.size() == 1)
				displayLexiconGui(new GuiLexiconEntry(visibleEntries.get(0)), true);
		} else {
			String currQuery = searchField.getText();
			searchField.textboxKeyTyped(typedChar, keyCode);
			if(!searchField.getText().equals(currQuery))
				buildEntryButtons();
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button instanceof GuiButtonCategory)
			displayLexiconGui(new GuiLexiconCategory(((GuiButtonCategory) button).getCategory()), true);
		else if(button instanceof GuiButtonEntry)
			GuiLexiconEntry.displayOrBookmark(this, ((GuiButtonEntry) button).getEntry());
	}
	
	@Override
	void onPageChanged() {
		buildEntryButtons();
	}
	
	void buildEntryButtons() {
		buttonList.removeAll(dependentButtons);
		dependentButtons.clear();
		visibleEntries.clear();
		
		allEntries.stream().filter((e) -> e.getName().toLowerCase().contains(searchField.getText().toLowerCase())).forEach(visibleEntries::add);

		maxpages = 1;
		int count = visibleEntries.size();
		count -= ENTRIES_IN_FIRST_PAGE;
		if(count > 0)
			maxpages += (int) Math.ceil((float) count / (ENTRIES_PER_PAGE * 2));
		
		while(getEntryCountStart() > visibleEntries.size())
			page--;
		
		if(page == 0) {
			addEntryButtons(RIGHT_PAGE_X, TOP_PADDING + 20, 0, ENTRIES_IN_FIRST_PAGE);
			addSubcategoryButtons();
		} else {
			int start = getEntryCountStart();
			addEntryButtons(LEFT_PAGE_X, TOP_PADDING, start, ENTRIES_PER_PAGE);
			addEntryButtons(RIGHT_PAGE_X, TOP_PADDING, start + ENTRIES_IN_FIRST_PAGE, ENTRIES_PER_PAGE);
		}
	}
	
	int getEntryCountStart() {
		if(page == 0)
			return 0;
		
		int start = ENTRIES_IN_FIRST_PAGE;
		start += (ENTRIES_PER_PAGE * 2) * (page - 1);
		return start;
	}
	
	void addEntryButtons(int x, int y, int start, int count) {
		for(int i = 0; i < count && (i + start) < visibleEntries.size(); i++) {
			GuiButton button = new GuiButtonEntry(this, bookLeft + x, bookTop + y + i * 11, visibleEntries.get(start + i), start + i);
			buttonList.add(button);
			dependentButtons.add(button);
		}
	}

}
