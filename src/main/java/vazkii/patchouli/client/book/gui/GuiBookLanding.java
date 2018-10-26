package vazkii.patchouli.client.book.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookAdvancements;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookConfig;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEdit;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookHistory;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookResize;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonIndex;
import vazkii.patchouli.client.gui.GuiAdvancementsExt;
import vazkii.patchouli.common.book.Book;

public class GuiBookLanding extends GuiBook {

	BookTextRenderer text;
	int loadedCategories = 0;

	public GuiBookLanding(Book book) {
		super(book);
	}

	@Override
	public void initGui() {
		super.initGui();

		text = new BookTextRenderer(this, I18n.translateToLocal(book.landingText), LEFT_PAGE_X, TOP_PADDING + 25);

		int x = bookLeft + 20;
		int y = bookTop + FULL_HEIGHT - 62;
		int dist = 15;
		int pos = 0;
		
		// Resize
		buttonList.add(new GuiButtonBookResize(this, x + (pos++) * dist, y, true));
		
		// History
		buttonList.add(new GuiButtonBookHistory(this, x + (pos++) * dist, y));

		// Advancements
		if(!book.advancementsTab.isEmpty())
			buttonList.add(new GuiButtonBookAdvancements(this, x + (pos++) * dist, y));
		
		// Config
		IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
		if(guiFactory != null && guiFactory.hasConfigGui())
			buttonList.add(new GuiButtonBookConfig(this, x + (pos++) * dist, y));
		
		if(Minecraft.getMinecraft().player.isCreative())
			buttonList.add(new GuiButtonBookEdit(this, x + (pos++) * dist, y));
		
		int i = 0;
		List<BookCategory> categories = new ArrayList(book.contents.categories.values());
		Collections.sort(categories);

		for(BookCategory category : categories) {	
			if(category.getParentCategory() != null)
				continue;

			addCategoryButton(i, category);
			i++;
		}
		addCategoryButton(i, null);
		loadedCategories = i + 1;
	}

	void addCategoryButton(int i, BookCategory category) {
		int x = RIGHT_PAGE_X + 10 + (i % 4) * 24;
		int y = TOP_PADDING + 25 + (i /4) * 24;

		if(category == null)
			buttonList.add(new GuiButtonIndex(this, x, y));	
		else buttonList.add(new GuiButtonCategory(this, x, y, category));
	}

	@Override
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		text.render(mouseX, mouseY);

		drawCenteredStringNoShadow(I18n.translateToLocal("patchouli.gui.lexicon.categories"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);

		int topSeparator = TOP_PADDING + 12;
		int bottomSeparator = topSeparator + 25 + 24 * (loadedCategories / 4 + 1);

		drawHeader();
		drawSeparator(book, RIGHT_PAGE_X, topSeparator);
		drawSeparator(book, RIGHT_PAGE_X, bottomSeparator);

		if(book.contents.isErrored())
			drawCenteredStringNoShadow(I18n.translateToLocal("patchouli.gui.lexicon.loading_error"), RIGHT_PAGE_X + PAGE_WIDTH / 2, bottomSeparator + 12, 0xFF0000);

		drawProgressBar(mouseX, mouseY, (e) -> true);
	}

	void drawHeader() {
		GlStateManager.color(1F, 1F, 1F, 1F);
		drawFromTexture(book, -8, 12, 0, 180, 140, 31);

		int color = book.nameplateColor;
		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.drawString(book.getBookItem().getDisplayName(), 13, 16, color);

		fontRenderer.setUnicodeFlag(true);
		fontRenderer.drawString(book.contents.getSubtitle(), 24, 24, color); 
		fontRenderer.setUnicodeFlag(unicode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		text.click(mouseX, mouseY, mouseButton);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button instanceof GuiButtonIndex)
			displayLexiconGui(new GuiBookIndex(book), true);

		else if(button instanceof GuiButtonCategory)
			displayLexiconGui(new GuiBookCategory(book, ((GuiButtonCategory) button).getCategory()), true);

		else if(button instanceof GuiButtonBookHistory)
			displayLexiconGui(new GuiBookHistory(book), true);

		else if(button instanceof GuiButtonBookConfig) {
			IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(book.owner);
			GuiScreen configGui = guiFactory.createConfigGui(this);
			mc.displayGuiScreen(configGui);
		}

		else if(button instanceof GuiButtonBookAdvancements)
			mc.displayGuiScreen(new GuiAdvancementsExt(mc.player.connection.getAdvancementManager(), this, book.advancementsTab));

		else if(button instanceof GuiButtonBookEdit) {
			if(isCtrlKeyDown()) {
				long time = System.currentTimeMillis();
				book.reloadContents();
				displayLexiconGui(new GuiBookLanding(book), false);
				mc.player.sendMessage(new TextComponentTranslation("patchouli.gui.lexicon.reloaded", (System.currentTimeMillis() - time)));
			} else displayLexiconGui(new GuiBookWriter(book), true);
		}

		else if(button instanceof GuiButtonBookResize) {
			if(PersistentData.data.bookGuiScale >= maxScale)
				PersistentData.data.bookGuiScale = 2;
			else PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);

			PersistentData.save();
			displayLexiconGui(this, false);
		}
	}

}
