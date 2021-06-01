package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.gui.button.*;
import vazkii.patchouli.client.gui.GuiAdvancementsExt;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiBookLanding extends GuiBook {

	BookTextRenderer text;
	int loadedCategories = 0;

	public GuiBookLanding(Book book) {
		super(book, new TranslatableText(book.name));
	}

	@Override
	public void init() {
		super.init();

		text = new BookTextRenderer(this, I18n.translate(book.landingText), LEFT_PAGE_X, TOP_PADDING + 25);

		boolean disableBar = !book.showProgress || !book.advancementsEnabled();

		int x = bookLeft + (disableBar ? 25 : 20);
		int y = bookTop + FULL_HEIGHT - (disableBar ? 25 : 62);
		int dist = 15;
		int pos = 0;

		// Resize
		if (maxScale > 2) {
			addDrawable(new GuiButtonBookResize(this, x + (pos++) * dist, y, true, this::handleButtonResize));
		}

		// History
		addDrawable(new GuiButtonBookHistory(this, x + (pos++) * dist, y, this::handleButtonHistory));

		// Advancements
		if (book.advancementsTab != null) {
			addDrawable(new GuiButtonBookAdvancements(this, x + (pos++) * dist, y, this::handleButtonAdvancements));
		}

		// Config
		//		if(!book.isExternal) {
		//			IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
		//			if(guiFactory != null && guiFactory.hasConfigGui())
		//				addButton(new GuiButtonBookConfig(this, x + (pos++) * dist, y));
		//		}

		if (MinecraftClient.getInstance().player.isCreative()) {
			addDrawable(new GuiButtonBookEdit(this, x + (pos++) * dist, y, this::handleButtonEdit));
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
			addDrawable(new GuiButtonIndex(this, x, y, this::handleButtonIndex));
		} else {
			addDrawable(new GuiButtonCategory(this, x, y, category, this::handleButtonCategory));
		}
	}

	@Override
	void drawForegroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		text.render(ms, mouseX, mouseY);

		drawCenteredStringNoShadow(ms, I18n.translate("patchouli.gui.lexicon.categories"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

		int topSeparator = TOP_PADDING + 12;
		int bottomSeparator = topSeparator + 25 + 24 * ((loadedCategories - 1) / 4 + 1);

		drawHeader(ms);
		drawSeparator(ms, book, RIGHT_PAGE_X, topSeparator);

		if (loadedCategories <= 16) {
			drawSeparator(ms, book, RIGHT_PAGE_X, bottomSeparator);
		}

		if (book.contents.isErrored()) {
			int x = RIGHT_PAGE_X + PAGE_WIDTH / 2;
			int y = bottomSeparator + 12;

			drawCenteredStringNoShadow(ms, I18n.translate("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
			drawCenteredStringNoShadow(ms, I18n.translate("patchouli.gui.lexicon.loading_error_hover"), x, y + 10, 0x777777);

			x -= PAGE_WIDTH / 2;
			y -= 4;

			if (isMouseInRelativeRange(mouseX, mouseY, x, y, PAGE_WIDTH, 20)) {
				makeErrorTooltip();
			}
		}

		drawProgressBar(ms, book, mouseX, mouseY, (e) -> true);
	}

	void drawHeader(MatrixStack ms) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		drawFromTexture(ms, book, -8, 12, 0, 180, 140, 31);

		int color = book.nameplateColor;
		textRenderer.draw(ms, book.getBookItem().getName(), 13, 16, color);
		Text toDraw = book.getSubtitle().fillStyle(book.getFontStyle());
		textRenderer.draw(ms, toDraw, 24, 24, color);
	}

	void makeErrorTooltip() {
		Throwable e = book.contents.getException();

		List<Text> lines = new ArrayList<>();
		while (e != null) {
			String msg = e.getMessage();
			if (msg != null && !msg.isEmpty()) {
				lines.add(new LiteralText(e.getMessage()));
			}
			e = e.getCause();
		}

		if (!lines.isEmpty()) {
			lines.add(new TranslatableText("patchouli.gui.lexicon.loading_error_log").formatted(Formatting.GREEN));
			setTooltip(lines);
		}
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		return text.click(mouseX, mouseY, mouseButton)
				|| super.mouseClickedScaled(mouseX, mouseY, mouseButton);
	}

	public void handleButtonIndex(ButtonWidget button) {
		displayLexiconGui(new GuiBookIndex(book), true);
	}

	public void handleButtonCategory(ButtonWidget button) {
		displayLexiconGui(new GuiBookCategory(book, ((GuiButtonCategory) button).getCategory()), true);
	}

	public void handleButtonHistory(ButtonWidget button) {
		displayLexiconGui(new GuiBookHistory(book), true);
	}

	public void handleButtonConfig(ButtonWidget button) {
//		IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
//		Screen configGui = guiFactory.createConfigGui(this);
//		mc.displayGuiScreen(configGui);
	}

	public void handleButtonAdvancements(ButtonWidget button) {
		client.openScreen(new GuiAdvancementsExt(client.player.networkHandler.getAdvancementHandler(), this, book.advancementsTab));
	}

	public void handleButtonEdit(ButtonWidget button) {
		if (hasShiftDown()) {
			long time = System.currentTimeMillis();
			book.reloadContentsAndExtensions();
			book.reloadLocks(false);
			displayLexiconGui(new GuiBookLanding(book), false);
			client.player.sendMessage(new TranslatableText("patchouli.gui.lexicon.reloaded", (System.currentTimeMillis() - time)), false);
		} else {
			displayLexiconGui(new GuiBookWriter(book), true);
		}
	}

	public void handleButtonResize(ButtonWidget button) {
		if (PersistentData.data.bookGuiScale >= maxScale) {
			PersistentData.data.bookGuiScale = 0;
		} else {
			PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);
		}

		PersistentData.save();
		displayLexiconGui(this, false);
	}

}
