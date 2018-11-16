package vazkii.patchouli.client.book.gui;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookArrow;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookBack;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookBookmark;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;

public abstract class GuiBook extends GuiScreen {

	public static final int FULL_WIDTH = 272;
	public static final int FULL_HEIGHT = 180;
	public static final int PAGE_WIDTH = 116;
	public static final int PAGE_HEIGHT = 156;
	public static final int TOP_PADDING = 18;
	public static final int LEFT_PAGE_X = 15;
	public static final int RIGHT_PAGE_X = 141;
	public static final int TEXT_LINE_HEIGHT = 9;
	public static final int MAX_BOOKMARKS = 10;

	public final Book book;
	
	private static int lastSound;
	public int bookLeft, bookTop;

	private List<String> tooltip;
	private ItemStack tooltipStack;
	private Pair<BookEntry, Integer> targetPage;
	protected int page = 0, maxpages = 0;

	public int ticksInBook;
	public int maxScale;
	
	boolean needsBookmarkUpdate = false;

	public GuiBook(Book book) {
		this.book = book;
	}
	
	@Override
	public void initGui() {
		int guiScale = mc.gameSettings.guiScale;
		maxScale = getMaxAllowedScale();
		int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);

		if(persistentScale > 0 && persistentScale != guiScale) {
			mc.gameSettings.guiScale = persistentScale;
			ScaledResolution res = new ScaledResolution(mc);
			width = res.getScaledWidth();
			height = res.getScaledHeight();
			mc.gameSettings.guiScale = guiScale;
		}
		
		bookLeft = width / 2 - FULL_WIDTH / 2;
		bookTop = height / 2 - FULL_HEIGHT / 2;

		book.contents.currentGui = this;

		buttonList.clear();

		buttonList.add(new GuiButtonBookBack(this, width / 2 - 9, bookTop + FULL_HEIGHT - 5));
		buttonList.add(new GuiButtonBookArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
		buttonList.add(new GuiButtonBookArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));
		
		addBookmarkButtons();
	}

	@Override
	public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution res = new ScaledResolution(mc);
		int guiScale = mc.gameSettings.guiScale;

		GlStateManager.pushMatrix();
		int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);

		if(persistentScale > 0 && persistentScale != guiScale) {
			mc.gameSettings.guiScale = persistentScale;
			float s = (float) persistentScale / (float) res.getScaleFactor();

			GlStateManager.scale(s, s, s);

			res = new ScaledResolution(mc);
			int sw = res.getScaledWidth();
			int sh = res.getScaledHeight();
			mouseX = Mouse.getX() * sw / mc.displayWidth;
			mouseY = sh - Mouse.getY() * sh / mc.displayHeight - 1;
		}

		drawScreenAfterScale(mouseX, mouseY, partialTicks);

		mc.gameSettings.guiScale = guiScale;
		GlStateManager.popMatrix();
	}

	final void drawScreenAfterScale(int mouseX, int mouseY, float partialTicks) {
		resetTooltip();
		drawDefaultBackground();

		GlStateManager.pushMatrix();
		GlStateManager.translate(bookLeft, bookTop, 0);
		GlStateManager.color(1F, 1F, 1F);
		drawBackgroundElements(mouseX, mouseY, partialTicks);
		drawForegroundElements(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();

		super.drawScreen(mouseX, mouseY, partialTicks);

		drawTooltip(mouseX, mouseY);
	}

	public void addBookmarkButtons() {
		buttonList.removeIf((b) -> b instanceof GuiButtonBookBookmark);
		int y = 0;
		List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
		for(int i = 0; i < bookmarks.size(); i++) {
			Bookmark bookmark = bookmarks.get(i);
			buttonList.add(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
			y += 12;
		}
		
		y += (y == 0 ? 0 : 2);
		if(shouldAddAddBookmarkButton() && bookmarks.size() <= MAX_BOOKMARKS)
			buttonList.add(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));

		if(MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null)
			buttonList.add(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 20, MultiblockVisualizationHandler.bookmark, true));
	}
	
	protected boolean shouldAddAddBookmarkButton() {
		return false;
	}
	
	public void bookmarkThis() {
		// NO-OP
	}
	
	public void onFirstOpened() {
		// NO-OP
	}
	
	@Override
	public void updateScreen() {
		if(!isShiftKeyDown())
			ticksInBook++;
		
		if(needsBookmarkUpdate) {
			needsBookmarkUpdate = false;
			addBookmarkButtons();
		}
	}

	final void drawBackgroundElements(int mouseX, int mouseY, float partialTicks) {
		drawFromTexture(book, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
	}

	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) { }

	final void drawTooltip(int mouseX, int mouseY) {
		if(tooltipStack != null) {
			List<String> tooltip = this.getItemToolTip(tooltipStack);

			Pair<BookEntry, Integer> provider = book.contents.getEntryForStack(tooltipStack);
			if(provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getLeft())) {
				tooltip.add(TextFormatting.GOLD + "(" + I18n.format("patchouli.gui.lexicon.shift_for_recipe") + ')');
				targetPage = provider;
			}

			GuiUtils.preItemToolTip(tooltipStack);
			FontRenderer font = tooltipStack.getItem().getFontRenderer(tooltipStack);
			this.drawHoveringText(tooltip, mouseX, mouseY, (font == null ? fontRenderer : font));
			GuiUtils.postItemToolTip();
		} else if(tooltip != null && !tooltip.isEmpty())
			GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, width, height, -1, fontRenderer);
	}

	final void resetTooltip() {
		tooltipStack = null;
		tooltip = null;
		targetPage = null;
	}

	public static void drawFromTexture(Book book, int x, int y, int u, int v, int w, int h) {
		Minecraft.getMinecraft().renderEngine.bindTexture(book.bookResource);
		drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, 512, 256);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof GuiButtonBookBack)
			back(false);
		else if(button instanceof GuiButtonBookArrow)
			changePage(((GuiButtonBookArrow) button).left, false);
		else if(button instanceof GuiButtonBookBookmark) {
			GuiButtonBookBookmark bookmarkButton = (GuiButtonBookBookmark) button;
			Bookmark bookmark = bookmarkButton.bookmark;
			if(bookmark == null || bookmark.getEntry(book) == null)
				bookmarkThis();
			else {
				if(isShiftKeyDown() && !bookmarkButton.multiblock) {
					List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
					bookmarks.remove(bookmark);
					PersistentData.save();
					needsBookmarkUpdate = true;
				} else displayLexiconGui(new GuiBookEntry(book, bookmark.getEntry(book), bookmark.page), true);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		switch(mouseButton) {
		case 0:
			if(targetPage != null && isShiftKeyDown()) {
				displayLexiconGui(new GuiBookEntry(book, targetPage.getLeft(), targetPage.getRight()), true);
				playBookFlipSound(book);
			}
			break;
		case 1: 
			back(true);
			break;
		case 3:  
			changePage(true, true);
			break;
		case 4:
			changePage(false, true);
			break;
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int w = Mouse.getEventDWheel();
		if(w < 0)
			changePage(false, true);
		else if(w > 0)
			changePage(true, true);
	}

	void back(boolean sfx) {
		if(!book.contents.guiStack.isEmpty()) {
			if(isShiftKeyDown()) {
				displayLexiconGui(new GuiBookLanding(book), false);
				book.contents.guiStack.clear();
			} else displayLexiconGui(book.contents.guiStack.pop(), false);
			
			if(sfx)
				playBookFlipSound(book);
		}
	}

	void changePage(boolean left, boolean sfx) {
		if(canSeePageButton(left)) {
			int oldpage = page;
			if(left)
				page--;
			else page++;

			onPageChanged();
			if(sfx)
				playBookFlipSound(book);
		}
	}

	void onPageChanged() {
		// NO-OP
	}
	
	public boolean canBeOpened() {
		return true;
	}

	public boolean canSeePageButton(boolean left) {
		return left ? page > 0 : (page + 1) < maxpages; 
	}

	public boolean canSeeBackButton() {
		return !book.contents.guiStack.isEmpty();
	}

	public void setTooltip(String... strings) {
		setTooltip(Arrays.asList(strings));
	}

	public void setTooltip(List<String> strings) {
		tooltip = strings;
	}

	public void setTooltipStack(ItemStack stack) {
		setTooltip();
		tooltipStack = stack;
	}

	public boolean isMouseInRelativeRange(int absMx, int absMy, int x, int y, int w, int h) {
		int mx = absMx - bookLeft;
		int my = absMy - bookTop;
		return mx > x && my > y && mx <= (x + w) && my <= (y + h);
	}

	public void drawProgressBar(Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
		if(!book.showProgress || PatchouliConfig.disableAdvancementLocking)
			return;
		
		int barLeft = 19;
		int barTop = FULL_HEIGHT - 36;
		int barWidth = PAGE_WIDTH - 10;
		int barHeight = 12;

		int totalEntries = 0;
		int unlockedEntries = 0;
		
		int unlockedSecretEntries = 0;
		
		for(BookEntry entry : book.contents.entries.values())
			if(filter.test(entry)) {
				if(entry.isSecret()) {
					if(!entry.isLocked())
						unlockedSecretEntries++;
				} else {
					totalEntries++;
					if(!entry.isLocked())
						unlockedEntries++;
				}
			}
		
		float unlockFract = (float) unlockedEntries / Math.max(1, (float) totalEntries);
		int progressWidth = (int) (((float) barWidth - 1) * unlockFract);

		drawRect(barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);
		
		drawGradient(barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
		drawGradient(barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);

		fontRenderer.drawString(I18n.format("patchouli.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor);

		if(isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
			List<String> tooltip = new ArrayList<>();
			String progressStr = I18n.format("patchouli.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
			tooltip.add(progressStr);
			
			if(unlockedSecretEntries > 0) {
				if(unlockedSecretEntries == 1)
					tooltip.add(TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.progress_tooltip.secret1"));
				else tooltip.add(TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries)); 
			}
			
			if(unlockedEntries != totalEntries)
				tooltip.add(TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.progress_tooltip.info"));
			
			setTooltip(tooltip);
		}
	}
	
	private void drawGradient(int x, int y, int w, int h, int color) {
		int darkerColor = new Color(color).darker().getRGB();
		drawGradientRect(x, y, w, h, color, darkerColor);
	}
	
	public void drawCenteredStringNoShadow(String s, int x, int y, int color) {
		fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, color);
	}

	int getMaxAllowedScale() {
		Minecraft mc = Minecraft.getMinecraft();
		int scale = mc.gameSettings.guiScale;
		mc.gameSettings.guiScale = 0;
		ScaledResolution res = new ScaledResolution(mc);
		mc.gameSettings.guiScale = scale;

		return res.getScaleFactor();
	}
	
	public List<GuiButton> getButtonList() {
		return buttonList;
	}

	public int getPage() {
		return page;
	}

	public static void drawSeparator(Book book, int x, int y) {
		int w = 110;
		int h = 3;
		int rx = x + PAGE_WIDTH / 2 - w / 2;

		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.8F);
		drawFromTexture(book, rx, y, 140, 180, w, h);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
	
	public static void drawLock(Book book, int x, int y) {
		drawFromTexture(book, x, y, 250, 180, 16, 16);
	}
	
	public static void drawWarning(Book book, int x, int y, int rand) {
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		float alpha = (float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F;
		GlStateManager.color(1F, 1F, 1F, alpha);
		drawFromTexture(book, x, y, 140, 197, 8, 8);
		GlStateManager.enableAlpha();
		GlStateManager.color(1F, 1F, 1F);
	}
	
	public static void drawPageFiller(Book book) {
		drawPageFiller(book, RIGHT_PAGE_X, TOP_PADDING);
	}
	
	public static void drawPageFiller(Book book, int x, int y) {
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(book.fillerResource);
		drawModalRectWithCustomSizedTexture(x + PAGE_WIDTH / 2 - 64, y + PAGE_HEIGHT / 2 - 74, 0, 0, 128, 128, 128, 128);
	}

	public static void playBookFlipSound(Book book) {
		if(ClientTicker.ticksInGame - lastSound > 6) {
			SoundEvent sfx = PatchouliSounds.getSound(book.flipSound, PatchouliSounds.book_flip);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sfx, (float) (0.7 + Math.random() * 0.3)));
			lastSound = ClientTicker.ticksInGame;
		}
	}

	public static void openWebLink(String address) {
		try {
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop").invoke(null);
			oclass.getMethod("browse", URI.class).invoke(object, new URI(address));
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void displayLexiconGui(GuiBook gui, boolean push) {
		book.contents.openLexiconGui(gui, push);
	}
	
}

