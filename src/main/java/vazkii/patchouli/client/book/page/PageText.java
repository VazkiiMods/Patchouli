package vazkii.patchouli.client.book.page;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageText extends PageWithText {
	
	@Override
	public int getTextHeight() {
		return pageNum == 0 ? 22 : -4;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		super.render(mouseX, mouseY, pticks);
		
		if(pageNum == 0) {
			boolean renderedSmol = false;
			String smolText = "";
			
			if(mc.gameSettings.advancedItemTooltips) {
				ResourceLocation res = parent.getEntry().getResource();
				smolText = res.toString();
			} else if(entry.isExtension()) {
				String name = entry.getTrueProvider().getOwnerName();
				smolText = I18n.format("patchouli.gui.lexicon.added_by", name);
			}

			if(!smolText.isEmpty()) {
				GlStateManager.scale(0.5F, 0.5F, 1F);
				parent.drawCenteredStringNoShadow(smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor);
				GlStateManager.scale(2F, 2F, 1F);
				renderedSmol = true;
			}
			
			parent.drawCenteredStringNoShadow(parent.getEntry().getName(), GuiBook.PAGE_WIDTH / 2, renderedSmol ? -3 : 0, book.headerColor);
			parent.drawSeparator(book, 0, 12);
		}
	}

}
