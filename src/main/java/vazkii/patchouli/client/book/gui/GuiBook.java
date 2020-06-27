package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;

import org.lwjgl.glfw.GLFW;

import vazkii.patchouli.api.BookDrawScreenEvent;
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
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.awt.Color;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

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

	private static long lastSound;
	public int bookLeft, bookTop;
	private float scaleFactor;

	private List<ITextComponent> tooltip;
	private ItemStack tooltipStack;
	private Pair<BookEntry, Integer> targetPage;
	protected int spread = 0, maxSpreads = 0;

	public int ticksInBook;
	public int maxScale;

	boolean needsBookmarkUpdate = false;

	public GuiBook(Book book, ITextComponent title) {
		super(title);

		this.book = book;
	}

	@Override
	public void func_231160_c_() {
		MainWindow res = getMinecraft().getMainWindow();
		double oldGuiScale = res.calcGuiScale(getMinecraft().gameSettings.guiScale, getMinecraft().getForceUnicodeFont());

		maxScale = getMaxAllowedScale();
		int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);
		double newGuiScale = res.calcGuiScale(persistentScale, getMinecraft().getForceUnicodeFont());

		if (persistentScale > 0 && newGuiScale != oldGuiScale) {
			scaleFactor = (float) newGuiScale / (float) res.getGuiScaleFactor();

			res.setGuiScale(newGuiScale);
			field_230708_k_ = res.getScaledWidth();
			field_230709_l_ = res.getScaledHeight();
			res.setGuiScale(oldGuiScale);
		} else {
			scaleFactor = 1;
		}

		bookLeft = field_230708_k_ / 2 - FULL_WIDTH / 2;
		bookTop = field_230709_l_ / 2 - FULL_HEIGHT / 2;

		book.contents.currentGui = this;

		func_230480_a_(new GuiButtonBookBack(this, field_230708_k_ / 2 - 9, bookTop + FULL_HEIGHT - 5));
		func_230480_a_(new GuiButtonBookArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
		func_230480_a_(new GuiButtonBookArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));

		addBookmarkButtons();
	}

	@Override
	public final void func_230430_a_(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		ms.push();
		if (scaleFactor != 1) {
			ms.scale(scaleFactor, scaleFactor, scaleFactor);

			mouseX /= scaleFactor;
			mouseY /= scaleFactor;
		}

		drawScreenAfterScale(ms, mouseX, mouseY, partialTicks);
		ms.pop();
	}

	final void drawScreenAfterScale(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		resetTooltip();
		func_230446_a_(ms);

		ms.push();
		ms.translate(bookLeft, bookTop, 0);
		RenderSystem.color3f(1F, 1F, 1F);
		drawBackgroundElements(ms, mouseX, mouseY, partialTicks);
		drawForegroundElements(ms, mouseX, mouseY, partialTicks);
		ms.pop();

		super.func_230430_a_(ms, mouseX, mouseY, partialTicks);

		MinecraftForge.EVENT_BUS.post(new BookDrawScreenEvent(this, this.book.id, mouseX, mouseY, partialTicks));

		drawTooltip(ms, mouseX, mouseY);
	}

	public void addBookmarkButtons() {
		removeButtonsIf((b) -> b instanceof GuiButtonBookBookmark);

		int y = 0;
		List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
		for (Bookmark bookmark : bookmarks) {
			func_230480_a_(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
			y += 12;
		}

		y += (y == 0 ? 0 : 2);
		if (shouldAddAddBookmarkButton() && bookmarks.size() <= MAX_BOOKMARKS) {
			func_230480_a_(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));
		}

		if (MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null) {
			func_230480_a_(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 20, MultiblockVisualizationHandler.bookmark, true));
		}
	}

	public void removeButtonsIf(Predicate<IGuiEventListener> pred) {
		field_230710_m_.removeIf(pred);
		field_230705_e_.removeIf(pred);
	}

	public void removeButtonsIn(Collection<?> coll) {
		removeButtonsIf(coll::contains);
	}

	@Override // make public
	public <T extends Widget> T func_230480_a_(T widget) {
		return super.func_230480_a_(widget);
	}

	@Override // make public
	public void func_238653_a_(MatrixStack matrices, @Nullable Style style, int mouseX, int mouseY) {
		super.func_238653_a_(matrices, style, mouseX, mouseY);
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
	public void func_231023_e_() {
		if (!func_231173_s_()) {
			ticksInBook++;
		}

		if (needsBookmarkUpdate) {
			needsBookmarkUpdate = false;
			addBookmarkButtons();
		}
	}

	final void drawBackgroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		drawFromTexture(ms, book, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
	}

	void drawForegroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {}

	final void drawTooltip(MatrixStack ms, int mouseX, int mouseY) {
		if (tooltipStack != null) {
			List<ITextComponent> tooltip = this.func_231151_a_(tooltipStack);

			Pair<BookEntry, Integer> provider = book.contents.getEntryForStack(tooltipStack);
			if (provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getFirst())) {
				ITextComponent t = new StringTextComponent("(")
						.func_230529_a_(new TranslationTextComponent("patchouli.gui.lexicon.shift_for_recipe"))
						.func_240702_b_(")")
						.func_240699_a_(TextFormatting.GOLD);
				tooltip.add(t);
				targetPage = provider;
			}

			GuiUtils.preItemToolTip(tooltipStack);
			this.func_238654_b_(ms, tooltip, mouseX, mouseY);
			GuiUtils.postItemToolTip();
		} else if (tooltip != null && !tooltip.isEmpty()) {
			this.func_238654_b_(ms, tooltip, mouseX, mouseY);
		}
	}

	final void resetTooltip() {
		tooltipStack = null;
		tooltip = null;
		targetPage = null;
	}

	public static void drawFromTexture(MatrixStack ms, Book book, int x, int y, int u, int v, int w, int h) {
		Minecraft.getInstance().textureManager.bindTexture(book.bookTexture);
		AbstractGui.func_238463_a_(ms, x, y, u, v, w, h, 512, 256);
	}

	@Override
	public boolean func_231177_au__() {
		return book.pauseGame;
	}

	public void handleButtonBack(Button button) {
		back(false);
	}

	public void handleButtonArrow(Button button) {
		changePage(((GuiButtonBookArrow) button).left, false);
	}

	public void handleButtonBookmark(Button button) {
		GuiButtonBookBookmark bookmarkButton = (GuiButtonBookBookmark) button;
		Bookmark bookmark = bookmarkButton.bookmark;
		if (bookmark == null || bookmark.getEntry(book) == null) {
			bookmarkThis();
		} else {
			if (func_231173_s_() && !bookmarkButton.multiblock) {
				List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
				bookmarks.remove(bookmark);
				PersistentData.save();
				needsBookmarkUpdate = true;
			} else {
				displayLexiconGui(new GuiBookEntry(book, bookmark.getEntry(book), bookmark.page), true);
			}
		}
	}

	@Override
	public final boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
		return mouseClickedScaled(mouseX / scaleFactor, mouseY / scaleFactor, mouseButton);
	}

	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		switch (mouseButton) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			if (targetPage != null && func_231173_s_()) {
				displayLexiconGui(new GuiBookEntry(book, targetPage.getFirst(), targetPage.getSecond()), true);
				playBookFlipSound(book);
				return true;
			}
			break;
		case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
			back(true);
			return true;
		case GLFW.GLFW_MOUSE_BUTTON_4:
			changePage(true, true);
			return true;
		case GLFW.GLFW_MOUSE_BUTTON_5:
			changePage(false, true);
			return true;
		}

		return super.func_231044_a_(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean func_231046_a_(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
			back(true);
			return true;
		} else {
			return super.func_231046_a_(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean func_231043_a_(double mouseX, double mouseY, double scroll) {
		if (scroll < 0) {
			changePage(false, true);
		} else if (scroll > 0) {
			changePage(true, true);
		}

		return true;
	}

	void back(boolean sfx) {
		if (!book.contents.guiStack.isEmpty()) {
			if (func_231173_s_()) {
				displayLexiconGui(new GuiBookLanding(book), false);
				book.contents.guiStack.clear();
			} else {
				displayLexiconGui(book.contents.guiStack.pop(), false);
			}

			if (sfx) {
				playBookFlipSound(book);
			}
		}
	}

	void changePage(boolean left, boolean sfx) {
		if (canSeePageButton(left)) {
			if (left) {
				spread--;
			} else {
				spread++;
			}

			onPageChanged();
			if (sfx) {
				playBookFlipSound(book);
			}
		}
	}

	void onPageChanged() {
		// NO-OP
	}

	public boolean canBeOpened() {
		return true;
	}

	public boolean canSeePageButton(boolean left) {
		return left ? spread > 0 : (spread + 1) < maxSpreads;
	}

	public boolean canSeeBackButton() {
		return !book.contents.guiStack.isEmpty();
	}

	public void setTooltip(ITextComponent... strings) {
		setTooltip(Arrays.asList(strings));
	}

	public void setTooltip(List<ITextComponent> strings) {
		tooltip = strings;
	}

	public void setTooltipStack(ItemStack stack) {
		setTooltip(Collections.emptyList());
		tooltipStack = stack;
	}

	public boolean isMouseInRelativeRange(double absMx, double absMy, int x, int y, int w, int h) {
		double mx = getRelativeX(absMx);
		double my = getRelativeY(absMy);

		return mx > x && my > y && mx <= (x + w) && my <= (y + h);
	}

	/**
	 * Convert the given argument from global screen coordinates to local coordinates
	 */
	public double getRelativeX(double absX) {
		return absX - bookLeft;
	}

	/**
	 * Convert the given argument from global screen coordinates to local coordinates
	 */
	public double getRelativeY(double absY) {
		return absY - bookTop;
	}

	public void drawProgressBar(MatrixStack ms, Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
		if (!book.showProgress || !book.advancementsEnabled()) {
			return;
		}

		int barLeft = 19;
		int barTop = FULL_HEIGHT - 36;
		int barWidth = PAGE_WIDTH - 10;
		int barHeight = 12;

		int totalEntries = 0;
		int unlockedEntries = 0;

		int unlockedSecretEntries = 0;

		for (BookEntry entry : book.contents.entries.values()) {
			if (filter.test(entry)) {
				if (entry.isSecret()) {
					if (!entry.isLocked()) {
						unlockedSecretEntries++;
					}
				} else {
					BookCategory category = entry.getCategory();
					if (category.isSecret() && !category.isLocked()) {
						continue;
					}

					totalEntries++;
					if (!entry.isLocked()) {
						unlockedEntries++;
					}
				}
			}
		}

		float unlockFract = (float) unlockedEntries / Math.max(1, (float) totalEntries);
		int progressWidth = (int) (((float) barWidth - 1) * unlockFract);

		AbstractGui.func_238467_a_(ms, barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);

		drawGradient(ms, barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
		drawGradient(ms, barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);

		field_230712_o_.func_238422_b_(ms, new TranslationTextComponent("patchouli.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor);

		if (isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
			List<ITextComponent> tooltip = new ArrayList<>();
			ITextComponent progressStr = new TranslationTextComponent("patchouli.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
			tooltip.add(progressStr);

			if (unlockedSecretEntries > 0) {
				if (unlockedSecretEntries == 1) {
					tooltip.add(new TranslationTextComponent("patchouli.gui.lexicon.progress_tooltip.secret1").func_240699_a_(TextFormatting.GRAY));
				} else {
					tooltip.add(new TranslationTextComponent("patchouli.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries).func_240699_a_(TextFormatting.GRAY));
				}
			}

			if (unlockedEntries != totalEntries) {
				tooltip.add(new TranslationTextComponent("patchouli.gui.lexicon.progress_tooltip.info").func_240699_a_(TextFormatting.GRAY));
			}

			setTooltip(tooltip);
		}
	}

	private void drawGradient(MatrixStack ms, int x, int y, int w, int h, int color) {
		int darkerColor = new Color(color).darker().getRGB();
		func_238468_a_(ms, x, y, w, h, color, darkerColor);
	}

	public void drawCenteredStringNoShadow(MatrixStack ms, ITextProperties s, int x, int y, int color) {
		field_230712_o_.func_238422_b_(ms, s, x - field_230712_o_.func_238414_a_(s) / 2.0F, y, color);
	}

	public void drawCenteredStringNoShadow(MatrixStack ms, String s, int x, int y, int color) {
		field_230712_o_.func_238421_b_(ms, s, x - field_230712_o_.getStringWidth(s) / 2.0F, y, color);
	}

	private int getMaxAllowedScale() {
		return getMinecraft().getMainWindow().calcGuiScale(0, getMinecraft().getForceUnicodeFont());
	}

	public int getSpread() {
		return spread;
	}

	public static void drawSeparator(MatrixStack ms, Book book, int x, int y) {
		int w = 110;
		int h = 3;
		int rx = x + PAGE_WIDTH / 2 - w / 2;

		RenderSystem.enableBlend();
		RenderSystem.color4f(1F, 1F, 1F, 0.8F);
		drawFromTexture(ms, book, rx, y, 140, 180, w, h);
		RenderSystem.color4f(1F, 1F, 1F, 1F);
	}

	public static void drawLock(MatrixStack ms, Book book, int x, int y) {
		drawFromTexture(ms, book, x, y, 250, 180, 16, 16);
	}

	public static void drawMarking(MatrixStack ms, Book book, int x, int y, int rand, EntryDisplayState state) {
		if (!state.hasIcon) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		float alpha = state.hasAnimation ? ((float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F) : 1F;
		RenderSystem.color4f(1F, 1F, 1F, alpha);
		drawFromTexture(ms, book, x, y, state.u, 197, 8, 8);
		RenderSystem.enableAlphaTest();
		RenderSystem.color3f(1F, 1F, 1F);
	}

	public static void drawPageFiller(MatrixStack ms, Book book) {
		drawPageFiller(ms, book, RIGHT_PAGE_X, TOP_PADDING);
	}

	public static void drawPageFiller(MatrixStack ms, Book book, int x, int y) {
		RenderSystem.enableBlend();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		Minecraft.getInstance().textureManager.bindTexture(book.fillerTexture);
		AbstractGui.func_238463_a_(ms, x + PAGE_WIDTH / 2 - 64, y + PAGE_HEIGHT / 2 - 74, 0, 0, 128, 128, 128, 128);
	}

	public static void playBookFlipSound(Book book) {
		if (ClientTicker.ticksInGame - lastSound > 6) {
			SoundEvent sfx = PatchouliSounds.getSound(book.flipSound, PatchouliSounds.book_flip);
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(sfx, (float) (0.7 + Math.random() * 0.3)));
			lastSound = ClientTicker.ticksInGame;
		}
	}

	public static void openWebLink(String address) {
		Util.getOSType().openURI(address);
	}

	public void displayLexiconGui(GuiBook gui, boolean push) {
		book.contents.openLexiconGui(gui, push);
	}

}
