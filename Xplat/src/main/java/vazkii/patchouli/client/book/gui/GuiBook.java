package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.*;
import vazkii.patchouli.client.book.gui.button.*;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorScreen;
import vazkii.patchouli.xplat.IXplatAbstractions;

import javax.annotation.Nullable;

import java.awt.*;
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

	private List<Component> tooltip;
	private ItemStack tooltipStack;
	private Pair<BookEntry, Integer> targetPage;
	protected int spread = 0, maxSpreads = 0;

	public int ticksInBook;
	public int maxScale;

	boolean needsBookmarkUpdate = false;

	public GuiBook(Book book, Component title) {
		super(title);

		this.book = book;
	}

	@Override
	public void init() {
		Window res = minecraft.getWindow();
		double oldGuiScale = res.calculateScale(minecraft.options.guiScale, minecraft.isEnforceUnicode());

		maxScale = getMaxAllowedScale();
		int persistentScale = Math.min(PersistentData.data.bookGuiScale, maxScale);
		double newGuiScale = res.calculateScale(persistentScale, minecraft.isEnforceUnicode());

		if (persistentScale > 0 && newGuiScale != oldGuiScale) {
			scaleFactor = (float) newGuiScale / (float) res.getGuiScale();

			res.setGuiScale(newGuiScale);
			width = res.getGuiScaledWidth();
			height = res.getGuiScaledHeight();
			res.setGuiScale(oldGuiScale);
		} else {
			scaleFactor = 1;
		}

		bookLeft = width / 2 - FULL_WIDTH / 2;
		bookTop = height / 2 - FULL_HEIGHT / 2;

		book.getContents().currentGui = this;

		addRenderableWidget(new GuiButtonBook(this, width / 2 - 9, bookTop + FULL_HEIGHT - 5, 308, 0, 18, 9, this::canSeeBackButton, this::handleButtonBack,
				new TranslatableComponent("patchouli.gui.lexicon.button.back"),
				new TranslatableComponent("patchouli.gui.lexicon.button.back.info").withStyle(ChatFormatting.GRAY)));
		addRenderableWidget(new GuiButtonBookArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
		addRenderableWidget(new GuiButtonBookArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));

		addBookmarkButtons();
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}

	@Override
	public final void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		ms.pushPose();
		if (scaleFactor != 1) {
			ms.scale(scaleFactor, scaleFactor, scaleFactor);

			mouseX /= scaleFactor;
			mouseY /= scaleFactor;
		}

		drawScreenAfterScale(ms, mouseX, mouseY, partialTicks);
		ms.popPose();
	}

	private void drawScreenAfterScale(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		resetTooltip();
		renderBackground(ms);

		ms.pushPose();
		ms.translate(bookLeft, bookTop, 0);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		drawBackgroundElements(ms, mouseX, mouseY, partialTicks);
		drawForegroundElements(ms, mouseX, mouseY, partialTicks);
		ms.popPose();

		super.render(ms, mouseX, mouseY, partialTicks);

		IXplatAbstractions.INSTANCE.fireDrawBookScreen(this.book.id, this, mouseX, mouseY, partialTicks, ms);

		drawTooltip(ms, mouseX, mouseY);
	}

	public void addBookmarkButtons() {
		removeDrawablesIf((b) -> b instanceof GuiButtonBookBookmark);

		int y = 0;
		List<Bookmark> bookmarks = PersistentData.data.getBookData(book).bookmarks;
		for (Bookmark bookmark : bookmarks) {
			addRenderableWidget(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
			y += 12;
		}

		if (shouldAddAddBookmarkButton() && bookmarks.size() <= MAX_BOOKMARKS) {
			addRenderableWidget(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));
		}

		if (MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null) {
			addRenderableWidget(new GuiButtonBookBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 20, MultiblockVisualizationHandler.bookmark, true));
		}

		if (shouldAddMarkReadButton()) {
			addRenderableWidget(new GuiButtonBookMarkRead(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 10));
		}
	}

	public final void removeDrawablesIf(Predicate<Widget> pred) {
		((AccessorScreen) (this)).getRenderables().removeIf(pred);
		children().removeIf(listener -> listener instanceof Widget w && pred.test(w));
		((AccessorScreen) (this)).getNarratables().removeIf(listener -> listener instanceof Widget w && pred.test(w));
	}

	public final void removeDrawablesIn(Collection<?> coll) {
		removeDrawablesIf(coll::contains);
	}

	@Override // make public
	public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T drawableElement) {
		return super.addRenderableWidget(drawableElement);
	}

	@Override // make public
	public void renderComponentHoverEffect(PoseStack matrices, @Nullable Style style, int mouseX, int mouseY) {
		super.renderComponentHoverEffect(matrices, style, mouseX, mouseY);
	}

	protected boolean shouldAddAddBookmarkButton() {
		return false;
	}

	protected boolean shouldAddMarkReadButton() {
		if (this instanceof GuiBookIndex) {
			return false;
		}
		return book.getContents().entries.values().stream().anyMatch(v -> !v.isLocked() && v.getReadState().equals(EntryDisplayState.UNREAD));
	}

	public void bookmarkThis() {
		// NO-OP
	}

	public void onFirstOpened() {
		// NO-OP
	}

	@Override
	public void tick() {
		if (!hasShiftDown()) {
			ticksInBook++;
		}

		if (needsBookmarkUpdate) {
			needsBookmarkUpdate = false;
			addBookmarkButtons();
		}
	}

	final void drawBackgroundElements(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		drawFromTexture(ms, book, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
	}

	void drawForegroundElements(PoseStack ms, int mouseX, int mouseY, float partialTicks) {}

	final void drawTooltip(PoseStack ms, int mouseX, int mouseY) {
		if (tooltipStack != null) {
			List<Component> tooltip = this.getTooltipFromItem(tooltipStack);

			Pair<BookEntry, Integer> provider = book.getContents().getEntryForStack(tooltipStack);
			if (provider != null && (!(this instanceof GuiBookEntry) || ((GuiBookEntry) this).entry != provider.getFirst())) {
				Component t = new TextComponent("(")
						.append(new TranslatableComponent("patchouli.gui.lexicon.shift_for_recipe"))
						.append(")")
						.withStyle(ChatFormatting.GOLD);
				tooltip.add(t);
				targetPage = provider;
			}

			this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
		} else if (tooltip != null && !tooltip.isEmpty()) {
			this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
		}
	}

	final void resetTooltip() {
		tooltipStack = null;
		tooltip = null;
		targetPage = null;
	}

	public static void drawFromTexture(PoseStack ms, Book book, int x, int y, int u, int v, int w, int h) {
		RenderSystem.setShaderTexture(0, book.bookTexture);
		blit(ms, x, y, u, v, w, h, 512, 256);
	}

	@Override
	public boolean isPauseScreen() {
		return book.pauseGame;
	}

	private void handleButtonBack(Button button) {
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
			if (hasShiftDown() && !bookmarkButton.multiblock) {
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
	public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return mouseClickedScaled(mouseX / scaleFactor, mouseY / scaleFactor, mouseButton);
	}

	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		switch (mouseButton) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			if (targetPage != null && hasShiftDown()) {
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

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode) && !this.canSeeBackButton()) {
			this.onClose();
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
			back(true);
			return true;
		} else if (IXplatAbstractions.INSTANCE.handleRecipeKeybind(keyCode, scanCode, tooltipStack)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if (scroll < 0) {
			changePage(false, true);
		} else if (scroll > 0) {
			changePage(true, true);
		}

		return true;
	}

	void back(boolean sfx) {
		if (!book.getContents().guiStack.isEmpty()) {
			if (hasShiftDown()) {
				displayLexiconGui(new GuiBookLanding(book), false);
				book.getContents().guiStack.clear();
			} else {
				displayLexiconGui(book.getContents().guiStack.pop(), false);
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
		return !book.getContents().guiStack.isEmpty();
	}

	public void setTooltip(Component... strings) {
		setTooltip(Arrays.asList(strings));
	}

	public void setTooltip(List<Component> strings) {
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

	public void drawProgressBar(PoseStack ms, Book book, int mouseX, int mouseY, Predicate<BookEntry> filter) {
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

		for (BookEntry entry : book.getContents().entries.values()) {
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

		fill(ms, barLeft, barTop, barLeft + barWidth, barTop + barHeight, book.headerColor);

		drawGradient(ms, barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, book.progressBarBackground);
		drawGradient(ms, barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, book.progressBarColor);

		font.draw(ms, new TranslatableComponent("patchouli.gui.lexicon.progress_meter"), barLeft, barTop - 9, book.headerColor);

		if (isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
			List<Component> tooltip = new ArrayList<>();
			Component progressStr = new TranslatableComponent("patchouli.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
			tooltip.add(progressStr);

			if (unlockedSecretEntries > 0) {
				if (unlockedSecretEntries == 1) {
					tooltip.add(new TranslatableComponent("patchouli.gui.lexicon.progress_tooltip.secret1").withStyle(ChatFormatting.GRAY));
				} else {
					tooltip.add(new TranslatableComponent("patchouli.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries).withStyle(ChatFormatting.GRAY));
				}
			}

			if (unlockedEntries != totalEntries) {
				tooltip.add(new TranslatableComponent("patchouli.gui.lexicon.progress_tooltip.info").withStyle(ChatFormatting.GRAY));
			}

			setTooltip(tooltip);
		}
	}

	private void drawGradient(PoseStack ms, int x, int y, int w, int h, int color) {
		int darkerColor = new Color(color).darker().getRGB();
		fillGradient(ms, x, y, w, h, color, darkerColor);
	}

	public void drawCenteredStringNoShadow(PoseStack ms, FormattedCharSequence s, int x, int y, int color) {
		font.draw(ms, s, x - font.width(s) / 2.0F, y, color);
	}

	public void drawCenteredStringNoShadow(PoseStack ms, String s, int x, int y, int color) {
		font.draw(ms, s, x - font.width(s) / 2.0F, y, color);
	}

	private int getMaxAllowedScale() {
		return minecraft.getWindow().calculateScale(0, minecraft.isEnforceUnicode());
	}

	public int getSpread() {
		return spread;
	}

	public static void drawSeparator(PoseStack ms, Book book, int x, int y) {
		int w = 110;
		int h = 3;
		int rx = x + PAGE_WIDTH / 2 - w / 2;

		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F);
		drawFromTexture(ms, book, rx, y, 140, 180, w, h);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	public static void drawLock(PoseStack ms, Book book, int x, int y) {
		drawFromTexture(ms, book, x, y, 250, 180, 16, 16);
	}

	public static void drawMarking(PoseStack ms, Book book, int x, int y, int rand, EntryDisplayState state) {
		if (!state.hasIcon) {
			return;
		}

		RenderSystem.enableBlend();
		//RenderSystem.disableAlphaTest();
		float alpha = state.hasAnimation ? ((float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F) : 1F;
		RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
		drawFromTexture(ms, book, x, y, state.u, 197, 8, 8);
		//RenderSystem.enableAlphaTest();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	public static void drawPageFiller(PoseStack ms, Book book) {
		drawPageFiller(ms, book, RIGHT_PAGE_X, TOP_PADDING);
	}

	public static void drawPageFiller(PoseStack ms, Book book, int x, int y) {
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.setShaderTexture(0, book.fillerTexture);
		blit(ms, x + PAGE_WIDTH / 2 - 64, y + PAGE_HEIGHT / 2 - 74, 0, 0, 128, 128, 128, 128);
	}

	public static void playBookFlipSound(Book book) {
		if (ClientTicker.ticksInGame - lastSound > 6) {
			SoundEvent sfx = PatchouliSounds.getSound(book.flipSound, PatchouliSounds.BOOK_FLIP);
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sfx, (float) (0.7 + Math.random() * 0.3)));
			lastSound = ClientTicker.ticksInGame;
		}
	}

	public static void openWebLink(Screen prevScreen, String address) {
		var mc = Minecraft.getInstance();
		mc.setScreen(new ConfirmLinkScreen(yes -> {
			if (yes) {
				Util.getPlatform().openUri(address);
			}

			mc.setScreen(prevScreen);
		}, address, false));
	}

	public void displayLexiconGui(GuiBook gui, boolean push) {
		book.getContents().openLexiconGui(gui, push);
	}

}
