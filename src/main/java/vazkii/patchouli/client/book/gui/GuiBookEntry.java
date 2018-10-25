package vazkii.patchouli.client.book.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;

public class GuiBookEntry extends GuiBook {

	BookEntry entry;
	BookPage leftPage, rightPage;

	public GuiBookEntry(Book book, BookEntry entry) {
		this(book, entry, 0);
	}

	public GuiBookEntry(Book book, BookEntry entry, int page) {
		super(book);
		this.entry = entry;
		this.page = page; 
	}

	@Override
	public void initGui() {
		super.initGui();

		maxpages = (int) Math.ceil((float) entry.getPages().size() / 2);
		setupPages();

	}
	
	@Override
	public void onFirstOpened() {
		super.onFirstOpened();

		boolean dirty = false;
		String key = entry.getResource().toString();
		
		BookData data = PersistentData.data.getBookData(book);
		
		if(!data.viewedEntries.contains(key)) {
			data.viewedEntries.add(key);
			dirty = true;
		}
		
		int index = data.history.indexOf(key);
		if(index != 0) {
			if(index > 0)
				data.history.remove(key);
			
			data.history.add(0, key);
			while(data.history.size() > GuiBookEntryList.ENTRIES_PER_PAGE)
				data.history.remove(GuiBookEntryList.ENTRIES_PER_PAGE);
			
			dirty = true;
		}

		if(dirty)
			PersistentData.save();
	}

	@Override
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		drawPage(leftPage, mouseX, mouseY, partialTicks);
		drawPage(rightPage, mouseX, mouseY, partialTicks);

		if(rightPage == null)
			drawPageFiller(leftPage.book);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		clickPage(leftPage, mouseX, mouseY, mouseButton);
		clickPage(rightPage, mouseX, mouseY, mouseButton);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		if((leftPage != null && leftPage.interceptButton(button)) || (rightPage != null && rightPage.interceptButton(button)))
			return;
		
		super.actionPerformed(button);
	}
	
	void drawPage(BookPage page, int mouseX, int mouseY, float pticks) {
		if(page == null)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.translate(page.left, page.top, 0);
		page.render(mouseX - page.left, mouseY - page.top, pticks);
		GlStateManager.popMatrix();
	}

	void clickPage(BookPage page, int mouseX, int mouseY, int mouseButton) {
		if(page != null)
			page.mouseClicked(mouseX - page.left, mouseY - page.top, mouseButton);
	}

	@Override
	void onPageChanged() {
		setupPages();
		needsBookmarkUpdate = true;
	}

	void setupPages() {
		if(leftPage != null)
			leftPage.onHidden(this);
		if(rightPage != null)
			rightPage.onHidden(this);
		
		List<BookPage> pages = entry.getPages();
		int leftNum = page * 2;
		int rightNum = (page * 2) + 1;

		leftPage  = leftNum  < pages.size() ? pages.get(leftNum)  : null;
		rightPage = rightNum < pages.size() ? pages.get(rightNum) : null;

		if(leftPage != null)
			leftPage.onDisplayed(this, LEFT_PAGE_X, TOP_PADDING);
		if(rightPage != null)
			rightPage.onDisplayed(this, RIGHT_PAGE_X, TOP_PADDING);
	}

	public BookEntry getEntry() {
		return entry;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof GuiBookEntry && ((GuiBookEntry) obj).entry == entry && ((GuiBookEntry) obj).page == page);
	}

	@Override
	public boolean canBeOpened() {
		return !entry.isLocked() && !equals(Minecraft.getMinecraft().currentScreen);
	}

	@Override
	protected boolean shouldAddAddBookmarkButton() {
		return !isBookmarkedAlready();
	}
	
	boolean isBookmarkedAlready() {
		String entryKey = entry.getResource().toString();
		BookData data = PersistentData.data.getBookData(book);
		
		for(Bookmark bookmark : data.bookmarks)
			if(bookmark.entry.equals(entryKey) && bookmark.page == page)
				return true;
		
		return false;
	}

	@Override
	public void bookmarkThis() {
		String entryKey = entry.getResource().toString();
		BookData data = PersistentData.data.getBookData(book);
		data.bookmarks.add(new Bookmark(entryKey, page));
		PersistentData.save();
		needsBookmarkUpdate = true;
	}
	
	public static void displayOrBookmark(GuiBook currGui, BookEntry entry) {
		Book book = currGui.book;
		GuiBookEntry gui = new GuiBookEntry(currGui.book, entry);
		
		if(GuiScreen.isShiftKeyDown()) {
			BookData data = PersistentData.data.getBookData(book);

			if(gui.isBookmarkedAlready()) {
				String key = entry.getResource().toString();
				data.bookmarks.removeIf((bm) -> bm.entry.equals(key) && bm.page == 0);
				PersistentData.save();
				currGui.needsBookmarkUpdate = true;
				return;
			} else if(data.bookmarks.size() < MAX_BOOKMARKS) {
				gui.bookmarkThis();
				currGui.needsBookmarkUpdate = true;
				return;
			}
		}
		
		book.contents.openLexiconGui(gui, true);
	}
	
}
