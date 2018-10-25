package vazkii.patchouli.client.book.page;

import net.minecraft.client.renderer.GlStateManager;
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
			boolean adv = mc.gameSettings.advancedItemTooltips;
			parent.drawCenteredStringNoShadow(parent.getEntry().getName(), GuiBook.PAGE_WIDTH / 2, adv ? -3 : 0, book.headerColor);
			parent.drawSeparator(book, 0, 12);
			
			if(adv) {
				ResourceLocation res = parent.getEntry().getResource();
				GlStateManager.scale(0.5F, 0.5F, 1F);
				parent.drawCenteredStringNoShadow(res.toString(), GuiBook.PAGE_WIDTH, 12, book.headerColor);
				GlStateManager.scale(2F, 2F, 1F);
			}
		}
	}

}
