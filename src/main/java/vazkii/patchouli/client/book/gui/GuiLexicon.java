package vazkii.patchouli.client.book.gui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookRegistry;
import vazkii.patchouli.client.book.gui.button.GuiButtonLexiconArrow;
import vazkii.patchouli.client.book.gui.button.GuiButtonLexiconBack;
import vazkii.patchouli.client.book.gui.button.GuiButtonLexiconBookmark;

public abstract class GuiLexicon extends GuiScreen {

	// TODO: support multiple books
	public static final ResourceLocation LEXICON_TEXTURE = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/lexicon/lexicon.png"); 
	public static final ResourceLocation FILLER_TEXTURE = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/lexicon/elements.png"); 
	public static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/lexicon/crafting.png"); 
	public static final ResourceLocation RITUAL_CIRCLE_TEXTURE = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/lexicon/ritual_circle.png"); 

	public static final int FULL_WIDTH = 272;
	public static final int FULL_HEIGHT = 180;
	public static final int PAGE_WIDTH = 116;
	public static final int PAGE_HEIGHT = 156;
	public static final int TOP_PADDING = 18;
	public static final int LEFT_PAGE_X = 15;
	public static final int RIGHT_PAGE_X = 141;
	public static final int TEXT_LINE_HEIGHT = 9;
	public static final int MAX_BOOKMARKS = 10;

	public static Stack<GuiLexicon> guiStack = new Stack();
	public static GuiLexicon currentGui;
	private static int lastSound;
	public int bookLeft, bookTop;

	private List<String> tooltip;
	private ItemStack tooltipStack;
	private Pair<BookEntry, Integer> targetPage;
	protected int page = 0, maxpages = 0;

	public int ticksInBook;
	public int maxScale;
	
	boolean needsBookmarkUpdate = false;

	public static GuiLexicon getCurrentGui() {
		if(currentGui == null)
			currentGui = new GuiLexiconLanding();

		return currentGui;
	}

	public static void onReload() {
		currentGui = null;
		guiStack.clear();
	}

	public static void displayLexiconGui(GuiLexicon gui, boolean push) {
		if(gui.canBeOpened()) {
			Minecraft mc = Minecraft.getMinecraft();
			if(push && mc.currentScreen instanceof GuiLexicon && gui != mc.currentScreen)
				guiStack.push((GuiLexicon) mc.currentScreen);

			mc.displayGuiScreen(gui);
			gui.onFirstOpened();
		}
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

		currentGui = this;

		buttonList.clear();

		buttonList.add(new GuiButtonLexiconBack(this, width / 2 - 9, bookTop + FULL_HEIGHT - 5));
		buttonList.add(new GuiButtonLexiconArrow(this, bookLeft - 4, bookTop + FULL_HEIGHT - 6, true));
		buttonList.add(new GuiButtonLexiconArrow(this, bookLeft + FULL_WIDTH - 14, bookTop + FULL_HEIGHT - 6, false));
		
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
		drawBackgroundElements(mouseX, mouseY, partialTicks);
		drawForegroundElements(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();

		super.drawScreen(mouseX, mouseY, partialTicks);

		drawTooltip(mouseX, mouseY);
	}

	public void addBookmarkButtons() {
		buttonList.removeIf((b) -> b instanceof GuiButtonLexiconBookmark);
		int y = 0;
		for(int i = 0; i < PersistentData.data.bookmarks.size(); i++) {
			Bookmark bookmark = PersistentData.data.bookmarks.get(i);
			buttonList.add(new GuiButtonLexiconBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, bookmark));
			y += 12;
		}
		
		y += (y == 0 ? 0 : 2);
		if(shouldAddAddBookmarkButton() && PersistentData.data.bookmarks.size() <= MAX_BOOKMARKS)
			buttonList.add(new GuiButtonLexiconBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + y, null));

		// TODO: port multiblock system
//		if(MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.bookmark != null)
//			buttonList.add(new GuiButtonLexiconBookmark(this, bookLeft + FULL_WIDTH, bookTop + TOP_PADDING + PAGE_HEIGHT - 20, MultiblockVisualizationHandler.bookmark, true));
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
		drawFromTexture(0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
	}

	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) { }

	final void drawTooltip(int mouseX, int mouseY) {
		if(tooltipStack != null) {
			renderToolTip(tooltipStack, mouseX, mouseY);

			Pair<BookEntry, Integer> provider = BookRegistry.INSTANCE.getEntryForStack(tooltipStack);
			if(provider != null && (!(this instanceof GuiLexiconEntry) || ((GuiLexiconEntry) this).entry != provider.getLeft())) {
				GuiUtils.drawHoveringText(Arrays.asList(TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.shift_for_recipe")),
						mouseX, mouseY - 20, width, height, -1, fontRenderer);
				targetPage = provider;
			}
		} else if(tooltip != null && !tooltip.isEmpty())
			GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, width, height, -1, fontRenderer);
	}

	final void resetTooltip() {
		tooltipStack = null;
		tooltip = null;
		targetPage = null;
	}

	public static void drawFromTexture(int x, int y, int u, int v, int w, int h) {
		Minecraft.getMinecraft().renderEngine.bindTexture(LEXICON_TEXTURE);
		drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, 512, 256);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof GuiButtonLexiconBack)
			back(false);
		else if(button instanceof GuiButtonLexiconArrow)
			changePage(((GuiButtonLexiconArrow) button).left, false);
		else if(button instanceof GuiButtonLexiconBookmark) {
			GuiButtonLexiconBookmark bookmarkButton = (GuiButtonLexiconBookmark) button;
			Bookmark bookmark = bookmarkButton.bookmark;
			if(bookmark == null || bookmark.getEntry() == null)
				bookmarkThis();
			else {
				if(isShiftKeyDown() && !bookmarkButton.multiblock) {
					PersistentData.data.bookmarks.remove(bookmark);
					PersistentData.save();
					needsBookmarkUpdate = true;
				} else displayLexiconGui(new GuiLexiconEntry(bookmark.getEntry(), bookmark.page), true);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		switch(mouseButton) {
		case 0:
			if(targetPage != null && isShiftKeyDown())
				displayLexiconGui(new GuiLexiconEntry(targetPage.getLeft(), targetPage.getRight()), true);
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
		if(!guiStack.isEmpty()) {
			if(isShiftKeyDown()) {
				displayLexiconGui(new GuiLexiconLanding(), false);
				guiStack.clear();
			} else displayLexiconGui(guiStack.pop(), false);
			
			if(sfx)
				playBookFlipSound();
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
				playBookFlipSound();
		}
	}

	void onPageChanged() {
		// NO-OP
	}
	
	boolean canBeOpened() {
		return true;
	}

	public boolean canSeePageButton(boolean left) {
		return left ? page > 0 : (page + 1) < maxpages; 
	}

	public boolean canSeeBackButton() {
		return !guiStack.isEmpty();
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

	public void drawProgressBar(int mouseX, int mouseY, Predicate<BookEntry> filter) {
		int barLeft = 19;
		int barTop = FULL_HEIGHT - 36;
		int barWidth = PAGE_WIDTH - 10;
		int barHeight = 12;

		int totalEntries = 0;
		int unlockedEntries = 0;
		
		int unlockedSecretEntries = 0;
		
		for(BookEntry entry : BookRegistry.INSTANCE.entries.values())
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

		drawRect(barLeft, barTop, barLeft + barWidth, barTop + barHeight, 0xFF333333);
		drawGradientRect(barLeft + 1, barTop + 1, barLeft + barWidth - 1, barTop + barHeight - 1, 0xFFDDDDDD, 0xFFBBBBBB);
		drawGradientRect(barLeft + 1, barTop + 1, barLeft + progressWidth, barTop + barHeight - 1, 0xFFFFFF55, 0xFFBBBB00);

		fontRenderer.drawString(I18n.translateToLocal("alquimia.gui.lexicon.progress_meter"), barLeft, barTop - 9, 0x444444);

		if(isMouseInRelativeRange(mouseX, mouseY, barLeft, barTop, barWidth, barHeight)) {
			List<String> tooltip = new ArrayList();
			String progressStr = I18n.translateToLocalFormatted("alquimia.gui.lexicon.progress_tooltip", unlockedEntries, totalEntries);
			tooltip.add(progressStr);
			
			if(unlockedSecretEntries > 0) {
				if(unlockedSecretEntries == 1)
					tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.progress_tooltip.secret1"));
				else tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted("alquimia.gui.lexicon.progress_tooltip.secret", unlockedSecretEntries)); 
			}
			
			if(unlockedEntries != totalEntries)
				tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.progress_tooltip.info"));
			
			setTooltip(tooltip);
		}
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

	public static void drawSeparator(int x, int y) {
		int w = 110;
		int h = 3;
		int rx = x + PAGE_WIDTH / 2 - w / 2;

		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 0.8F);
		drawFromTexture(rx, y, 140, 180, w, h);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
	
	public static void drawLock(int x, int y) {
		drawFromTexture(x, y, 250, 180, 16, 16);
	}
	
	public static void drawWarning(int x, int y, int rand) {
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		float alpha = (float) Math.sin(ClientTicker.total * 0.2F) * 0.3F + 0.7F;
		GlStateManager.color(1F, 1F, 1F, alpha);
		drawFromTexture(x, y, 140, 197, 8, 8);
		GlStateManager.enableAlpha();
		GlStateManager.color(1F, 1F, 1F);
	}
	
	public static void drawPageFiller() {
		drawPageFiller(RIGHT_PAGE_X, TOP_PADDING);
	}
	
	public static void drawPageFiller(int x, int y) {
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(FILLER_TEXTURE);
		drawModalRectWithCustomSizedTexture(x + PAGE_WIDTH / 2 - 64, y + PAGE_HEIGHT / 2 - 74, 0, 0, 128, 128, 128, 128);
	}

	// TODO bring back sfx
	public static void playBookFlipSound() {
//		if(ClientTicker.ticksInGame - lastSound > 6) {
//			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(AlquimiaSounds.book_flip, (float) (0.7 + Math.random() * 0.3)));
//			lastSound = ClientTicker.ticksInGame;
//		}
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
	
}

