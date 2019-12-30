package vazkii.patchouli.client.book.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.TextFormat;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import vazkii.patchouli.api.BookDrawScreenCallback;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookArrow;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookBack;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookBookmark;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;

public abstract class GuiBook extends Screen {

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
	private float scaleFactor;

	private List<String> tooltip;
	private ItemStack tooltipStack;
	private Pair<BookEntry, Integer> targetPage;
	protected int page = 0, maxpages = 0;

	public int ticksInBook;
	public int maxScale;

	boolean needsBookmarkUpdate = false;

	public GuiBook(Book book) {
		super(new LiteralText(""));

		this.book = book;
	}

	public void init() {
		int guiScale = minecraft.options.guiScale;
		maxScale = getMaxAllowedScale();
		int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);

		if(persistentScale > 0 && persistentScale != guiScale) {
			Window res = minecraft.getWindow();
			scaleFactor = (float) persistentScale / (float) res.getScaleFactor();

			res.setScaleFactor(persistentScale);
			width = res.getScaledWidth();
			height = res.getScaledHeight();
			res.setScaleFactor(guiScale);
		} else scaleFactor = 1;

		bookLeft = width / 2 - FULL_WIDTH / 2;
		bookTop = height / 2 - FULL_HEIGHT / 2;

		book.contents.currentGui = this;

		clearButtons();

		addButton(new GuiButtonBookBack(this, width / 2 - 9, bookTop + FULL_HEIGHT - 5));
		addButton(new GuiButtonBookArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
		addButton(new GuiButtonBookArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));

		addBookmarkButtons();
	}

	public MinecraftClient getMinecraft() {
		return minecraft;
	}

	@Override
	public final void render(int mouseX, int mouseY, float partialTicks) {
		RenderSystem.pushMatrix();
		if(scaleFactor != 1) {
			RenderSystem.scalef(scaleFactor, scaleFactor, scaleFactor);

			mouseX /= scaleFactor;
			mouseY /= scaleFactor;
		}

		drawScreenAfterScale(mouseX, mouseY, partialTicks);
		RenderSystem.popMatrix();
	}

	final void drawScreenAfterScale(int mouseX, int mouseY, float partialTicks) {
		resetTooltip();
		renderBackground();

		RenderSystem.pushMatrix();
		RenderSystem.translatef(bookLeft, bookTop, 0);
		RenderSystem.color3f(1F, 1F, 1F);
		drawBackgroundElements(mouseX, mouseY, partialTicks);
		drawForegroundElements(mouseX, mouseY, partialTicks);
		RenderSystem.popMatrix();

		super.render(mouseX, mouseY, partialTicks);

		BookDrawScreenCallback.EVENT.invoker().trigger(this.book.resourceLoc, this, mouseX, mouseY, partialTicks);

		drawTooltip(mouseX, mouseY);
	}

	public void addBookmarkButtons() {
		removeButtonsIf((b) -> b instanceof GuiButtonBookBookmark);

		int y = 0;
		List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
		for(int i = 0; i < bookmarks.size(); i++) {
			Bookmark bookmark = bookmarks.get(i);
			addButton(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
			y += 12;
		}

		y += (y == 0 ? 0 : 2);
		if(shouldAddAddBookmarkButton() && bookmarks.size() <= MAX_BOOKMARKS)
			addButton(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));

		if(MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null)
			addButton(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 20, MultiblockVisualizationHandler.bookmark, true));
	}

	public void removeButtonsIf(Predicate<Element> pred) {
		buttons.removeIf(pred);
		children.removeIf(pred);
	}

	public void removeButtonsIn(Collection<?> coll) {
		removeButtonsIf(e -> coll.contains(e));
	}

	@Override // make public
	public <T extends AbstractButtonWidget> T addButton(T p_addButton_1_) {
		return super.addButton(p_addButton_1_);
	}

	protected void clearButtons() {
		buttons.clear();
		children.clear();
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
	public void tick() {
		if(!hasShiftDown())
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
			List<String> tooltip = this.getTooltipFromItem(tooltipStack);

			Pair<BookEntry, Integer> provider = book.contents.getEntryForStack(tooltipStack);
			if(provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getLeft())) {
				tooltip.add(TextFormat.GOLD + "(" + I18n.translate("patchouli.gui.lexicon.shift_for_recipe") + ')');
				targetPage = provider;
			}

			this.renderTooltip(tooltip, mouseX, mouseY);
		} else if(tooltip != null && !tooltip.isEmpty()) {
			List<String> wrappedTooltip = new ArrayList<>();
			for (String s : tooltip)
				Collections.addAll(wrappedTooltip, s.split("\n"));
			this.renderTooltip(wrappedTooltip, mouseX, mouseY);
		}
	}

	final void resetTooltip() {
		tooltipStack = null;
		tooltip = null;
		targetPage = null;
	}

	public static void drawFromTexture(Book book, int x, int y, int u, int v, int w, int h) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(book.bookResource);
		blit(x, y, u, v, w, h, 512, 256);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public void handleButtonBack(ButtonWidget button) {
		back(false);
	}

	public void handleButtonArrow(ButtonWidget button) {
		changePage(((GuiButtonBookArrow) button).left, false);
	}

	public void handleButtonBookmark(ButtonWidget button) {
		GuiButtonBookBookmark bookmarkButton = (GuiButtonBookBookmark) button;
		Bookmark bookmark = bookmarkButton.bookmark;
		if(bookmark == null || bookmark.getEntry(book) == null)
			bookmarkThis();
		else {
			if(hasShiftDown() && !bookmarkButton.multiblock) {
				List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
				bookmarks.remove(bookmark);
				PersistentData.save();
				needsBookmarkUpdate = true;
			} else displayLexiconGui(new GuiBookEntry(book, bookmark.getEntry(book), bookmark.page), true);
		}
	}

	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return mouseClickedScaled(mouseX / scaleFactor, mouseY / scaleFactor, mouseButton);
	}

	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		switch(mouseButton) {
		case 0:
			if(targetPage != null && hasShiftDown()) {
				displayLexiconGui(new GuiBookEntry(book, targetPage.getLeft(), targetPage.getRight()), true);
				playBookFlipSound(book);
				return true;
			}
			break;
		case 1: 
			back(true);
			return true;
		case 3:  
			changePage(true, true);
			return true;
		case 4:
			changePage(false, true);
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if(scroll < 0)
			changePage(false, true);
		else if(scroll > 0)
			changePage(true, true);

		return true;
	}

	void back(boolean sfx) {
		if(!book.contents.guiStack.isEmpty()) {
			if(hasShiftDown()) {
				displayLexiconGui(new GuiBookLanding(book), false);
				book.contents.guiStack.clear();
			} else displayLexiconGui(book.contents.guiStack.pop(), false);

			if(sfx)
				playBookFlipSound(book);
		}
	}

	void changePage(boolean left, boolean sfx) {
		if(canSeePageButton(left)) {
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

	public boolean isMouseInRelativeRange(double absMx, double absMy, int x, int y, int w, int h) {
		double mx = absMx - bookLeft * scaleFactor;
		double my = absMy - bookTop * scaleFactor;

		return mx > x && my > y && mx <= (x + w) && my <= (y + h);
	}

	public void drawProgressBar(Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
		if(!book.showProgress || PatchouliConfig.disableAdvancementLocking.get())
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
					BookCategory category = entry.getCategory();
					if(category.isSecret() && !category.isLocked())
						continue;

					totalEntries++;
					if(!entry.isLocked())
						unlockedEntries++;
				}
			}

		float unlockFract = (float) unlockedEntries / Math.max(1, (float) totalEntries);
		int progressWidth = (int) (((float) barWidth - 1) * unlockFract);

		fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);

		drawGradient(barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
		drawGradient(barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);

		font.draw(I18n.translate("patchouli.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor);

		if(isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
			List<String> tooltip = new ArrayList<>();
			String progressStr = I18n.translate("patchouli.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
			tooltip.add(progressStr);

			if(unlockedSecretEntries > 0) {
				if(unlockedSecretEntries == 1)
					tooltip.add(TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.progress_tooltip.secret1"));
				else tooltip.add(TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries));
			}

			if(unlockedEntries != totalEntries)
				tooltip.add(TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.progress_tooltip.info"));

			setTooltip(tooltip);
		}
	}

	private void drawGradient(int x, int y, int w, int h, int color) {
		int darkerColor = new Color(color).darker().getRGB();
		fillGradient(x, y, w, h, color, darkerColor);
	}

	public void drawCenteredStringNoShadow(String s, int x, int y, int color) {
		font.draw(s, x - font.getStringWidth(s) / 2, y, color);
	}

	int getMaxAllowedScale() {
		MinecraftClient mc = MinecraftClient.getInstance();
		return mc.getWindow().calculateScaleFactor(0, mc.forcesUnicodeFont());
	}

	public int getPage() {
		return page;
	}

	public static void drawSeparator(Book book, int x, int y) {
		int w = 110;
		int h = 3;
		int rx = x + PAGE_WIDTH / 2 - w / 2;

		RenderSystem.enableBlend();
		RenderSystem.color4f(1F, 1F, 1F, 0.8F);
		drawFromTexture(book, rx, y, 140, 180, w, h);
		RenderSystem.color4f(1F, 1F, 1F, 1F);
	}

	public static void drawLock(Book book, int x, int y) {
		drawFromTexture(book, x, y, 250, 180, 16, 16);
	}


	public static void drawMarking(Book book, int x, int y, int rand, EntryDisplayState state) {
		if(!state.hasIcon)
			return;

		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		float alpha = state.hasAnimation ? ((float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F) : 1F;
		RenderSystem.color4f(1F, 1F, 1F, alpha);
		drawFromTexture(book, x, y, state.u, 197, 8, 8);
		RenderSystem.enableAlphaTest();
		RenderSystem.color3f(1F, 1F, 1F);
	}

	public static void drawPageFiller(Book book) {
		drawPageFiller(book, RIGHT_PAGE_X, TOP_PADDING);
	}

	public static void drawPageFiller(Book book, int x, int y) {
		RenderSystem.enableBlend();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(book.fillerResource);
		blit(x + PAGE_WIDTH / 2 - 64, y + PAGE_HEIGHT / 2 - 74, 0, 0, 128, 128, 128, 128);
	}

	public static void playBookFlipSound(Book book) {
		if(ClientTicker.ticksInGame - lastSound > 6) {
			SoundEvent sfx = PatchouliSounds.getSound(book.flipSound, PatchouliSounds.book_flip);
			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(sfx, (float) (0.7 + Math.random() * 0.3)));
			lastSound = ClientTicker.ticksInGame;
		}
	}

	public static void openWebLink(String address) {
		Util.getOperatingSystem().open(address);
	}

	public void displayLexiconGui(GuiBook gui, boolean push) {
		book.contents.openLexiconGui(gui, push);
	}

}

