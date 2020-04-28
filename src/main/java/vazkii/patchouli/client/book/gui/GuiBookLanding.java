package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookAdvancements;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEdit;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookHistory;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookResize;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonIndex;
import vazkii.patchouli.client.gui.GuiAdvancementsExt;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GuiBookLanding extends GuiBook {

	BookTextRenderer text;
	int loadedCategories = 0;

	public GuiBookLanding(Book book) {
		super(book, new TranslationTextComponent(book.name));
	}

	@Override
	public void init() {
		super.init();

		text = new BookTextRenderer(this, I18n.format(book.landingText), LEFT_PAGE_X, TOP_PADDING + 25);

		boolean disableBar = !book.showProgress || PatchouliConfig.disableAdvancementLocking.get();

		int x = bookLeft + (disableBar ? 25 : 20);
		int y = bookTop + FULL_HEIGHT - (disableBar ? 25 : 62);
		int dist = 15;
		int pos = 0;

		// Resize
		if (maxScale > 2) {
			addButton(new GuiButtonBookResize(this, x + (pos++) * dist, y, true, this::handleButtonResize));
		}

		// History
		addButton(new GuiButtonBookHistory(this, x + (pos++) * dist, y, this::handleButtonHistory));

		// Advancements
		if (book.advancementsTab != null) {
			addButton(new GuiButtonBookAdvancements(this, x + (pos++) * dist, y, this::handleButtonAdvancements));
		}

		// Config
		//		if(!book.isExternal) {
		//			IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
		//			if(guiFactory != null && guiFactory.hasConfigGui())
		//				addButton(new GuiButtonBookConfig(this, x + (pos++) * dist, y));
		//		}

		if (Minecraft.getInstance().player.isCreative()) {
			addButton(new GuiButtonBookEdit(this, x + (pos++) * dist, y, this::handleButtonEdit));
		}

		int i = 0;
		List<BookCategory> categories = new ArrayList<>(book.contents.categories.values());
		Collections.sort(categories);

		for (BookCategory category : categories) {
			if (category.getParentCategory() != null || category.shouldHide()) {
				continue;
			}

			addCategoryButton(i, category);
			i++;
		}
		addCategoryButton(i, null);
		loadedCategories = i + 1;
	}

	void addCategoryButton(int i, BookCategory category) {
		int x = RIGHT_PAGE_X + 10 + (i % 4) * 24;
		int y = TOP_PADDING + 25 + (i / 4) * 24;

		if (category == null) {
			addButton(new GuiButtonIndex(this, x, y, this::handleButtonIndex));
		} else {
			addButton(new GuiButtonCategory(this, x, y, category, this::handleButtonCategory));
		}
	}

	@Override
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		text.render(mouseX, mouseY);

		drawCenteredStringNoShadow(I18n.format("patchouli.gui.lexicon.categories"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

		int topSeparator = TOP_PADDING + 12;
		int bottomSeparator = topSeparator + 25 + 24 * ((loadedCategories - 1) / 4 + 1);

		drawHeader();
		drawSeparator(book, RIGHT_PAGE_X, topSeparator);

		if (loadedCategories <= 16) {
			drawSeparator(book, RIGHT_PAGE_X, bottomSeparator);
		}

		if (book.contents.isErrored()) {
			int x = RIGHT_PAGE_X + PAGE_WIDTH / 2;
			int y = bottomSeparator + 12;

			drawCenteredStringNoShadow(I18n.format("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
			drawCenteredStringNoShadow(I18n.format("patchouli.gui.lexicon.loading_error_hover"), x, y + 10, 0x777777);

			x -= PAGE_WIDTH / 2;
			y -= 4;

			if (isMouseInRelativeRange(mouseX, mouseY, x, y, PAGE_WIDTH, 20)) {
				makeErrorTooltip();
			}
		}

		drawProgressBar(book, mouseX, mouseY, (e) -> true);
	}

	void drawHeader() {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		drawFromTexture(book, -8, 12, 0, 180, 140, 31);

		int color = book.nameplateColor;
		font.drawString(book.getBookItem().getDisplayName().getFormattedText(), 13, 16, color);
		book.getFont().drawString(book.contents.getSubtitle(), 24, 24, color);
	}

	void makeErrorTooltip() {
		Throwable e = book.contents.getException();

		List<String> lines = new ArrayList<>();
		while (e != null) {
			String msg = e.getMessage();
			if (msg != null && !msg.isEmpty()) {
				lines.add(e.getMessage());
			}
			e = e.getCause();
		}

		if (!lines.isEmpty()) {
			lines.add(TextFormatting.GREEN + I18n.format("patchouli.gui.lexicon.loading_error_log"));
			setTooltip(lines);
		}
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		return text.click(mouseX, mouseY, mouseButton)
				|| super.mouseClickedScaled(mouseX, mouseY, mouseButton);
	}

	public void handleButtonIndex(Button button) {
		displayLexiconGui(new GuiBookIndex(book), true);
	}

	public void handleButtonCategory(Button button) {
		displayLexiconGui(new GuiBookCategory(book, ((GuiButtonCategory) button).getCategory()), true);
	}

	public void handleButtonHistory(Button button) {
		displayLexiconGui(new GuiBookHistory(book), true);
	}

	public void handleButtonConfig(Button button) {
//		IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
//		Screen configGui = guiFactory.createConfigGui(this);
//		mc.displayGuiScreen(configGui);
	}

	public void handleButtonAdvancements(Button button) {
		minecraft.displayGuiScreen(new GuiAdvancementsExt(minecraft.player.connection.getAdvancementManager(), this, book.advancementsTab));
	}

	public void handleButtonEdit(Button button) {
		if (hasShiftDown()) {
			long time = System.currentTimeMillis();
			book.reloadContentsAndExtensions();
			book.reloadLocks(false);
			displayLexiconGui(new GuiBookLanding(book), false);
			minecraft.player.sendMessage(new TranslationTextComponent("patchouli.gui.lexicon.reloaded", (System.currentTimeMillis() - time)));
		} else {
			displayLexiconGui(new GuiBookWriter(book), true);
		}
	}

	public void handleButtonResize(Button button) {
		if (PersistentData.data.bookGuiScale >= maxScale) {
			PersistentData.data.bookGuiScale = 0;
		} else {
			PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);
		}

		PersistentData.save();
		displayLexiconGui(this, false);
	}

}
