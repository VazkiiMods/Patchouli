package vazkii.patchouli.client.book.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookRegistry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.client.book.gui.button.GuiButtonIndex;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookAdvancements;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookConfig;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEdit;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookHistory;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookResize;

public class GuiBookLanding extends GuiBook {

	BookTextRenderer text;
	int loadedCategories = 0;
	
	@Override
	public void initGui() {
		super.initGui();
		
		text = new BookTextRenderer(this, I18n.translateToLocal("alquimia.gui.lexicon.landing_info"), LEFT_PAGE_X, TOP_PADDING + 25);

		int x = bookLeft + 20;
		int y = bookTop + FULL_HEIGHT - 62;
		int dist = 15;
		buttonList.add(new GuiButtonBookResize(this, x + dist * 0, y, true));
		buttonList.add(new GuiButtonBookHistory(this, x + dist * 1, y));
		buttonList.add(new GuiButtonBookAdvancements(this, x + dist * 2, y));
		buttonList.add(new GuiButtonBookConfig(this, x + dist * 3, y));
		buttonList.add(new GuiButtonBookEdit(this, x + dist * 4, y));
		
		int i = 0;
		List<BookCategory> categories = new ArrayList(BookRegistry.INSTANCE.categories.values());
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
		
		drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.categories"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, 0x333333);
		
		int topSeparator = TOP_PADDING + 12;
		int bottomSeparator = topSeparator + 25 + 24 * (loadedCategories / 4 + 1);
		
		drawHeader();
		drawSeparator(RIGHT_PAGE_X, topSeparator);
		drawSeparator(RIGHT_PAGE_X, bottomSeparator);
		
		if(BookRegistry.INSTANCE.isErrored())
			drawCenteredStringNoShadow(I18n.translateToLocal("alquimia.gui.lexicon.loading_error"), RIGHT_PAGE_X + PAGE_WIDTH / 2, bottomSeparator + 12, 0xFF0000);
		
		drawProgressBar(mouseX, mouseY, (e) -> true);
	}
	
	void drawHeader() {
		GlStateManager.color(1F, 1F, 1F, 1F);
		drawFromTexture(-8, 12, 0, 180, 140, 31);

		int color = 0xFFDD00;
		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.drawString(new ItemStack(Items.BOOK).getDisplayName(), 13, 16, color); // TODO: support multiple books
		
		// TODO: support subtitle
//		fontRenderer.setUnicodeFlag(true);
//		fontRenderer.drawString(ItemLexicon.getEdition(), 24, 24, color); 
//		fontRenderer.setUnicodeFlag(unicode);
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
			displayLexiconGui(new GuiBookIndex(), true);
		else if(button instanceof GuiButtonCategory)
			displayLexiconGui(new GuiBookCategory(((GuiButtonCategory) button).getCategory()), true);
		else if(button instanceof GuiButtonBookHistory)
			displayLexiconGui(new GuiBookHistory(), true); // TODO bring back config and advancements
//		else if(button instanceof GuiButtonLexiconConfig)
//			mc.displayGuiScreen(new GuiFactory.GuiAlquimiaConfig(this));
//		else if(button instanceof GuiButtonLexiconAdvancements)
//			mc.displayGuiScreen(new GuiAdvancementsExt(mc.player.connection.getAdvancementManager(), this));
		else if(button instanceof GuiButtonBookEdit) {
			if(isCtrlKeyDown()) {
				long time = System.currentTimeMillis();
				BookRegistry.INSTANCE.reloadLexiconRegistry();
				displayLexiconGui(new GuiBookLanding(), false);
				mc.player.sendMessage(new TextComponentTranslation("alquimia.gui.lexicon.reloaded", (System.currentTimeMillis() - time)));
			} else displayLexiconGui(new GuiBookWriter(), true);
		} else if(button instanceof GuiButtonBookResize) {
			if(PersistentData.data.bookGuiScale >= maxScale)
				PersistentData.data.bookGuiScale = 2;
			else PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);

			PersistentData.save();
			displayLexiconGui(this, false);
		}
	}

}
