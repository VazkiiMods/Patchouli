package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.gui.GuiLexicon;
import vazkii.patchouli.client.book.gui.GuiLexiconEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonLexiconArrowSmall;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageImage extends PageWithText {

	String images[];
	String title;
	boolean border;

	transient ResourceLocation[] imageRes;
	transient int index;
	
	@Override
	public void onDisplayed(GuiLexiconEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		imageRes = new ResourceLocation[images.length];
		for(int i = 0; i < images.length; i++)
			imageRes[i] = new ResourceLocation(images[i]);
		
		int x = 90;
		int y = 100;
		adddButton(new GuiButtonLexiconArrowSmall(parent, x, y, true, () -> index > 0));
		adddButton(new GuiButtonLexiconArrowSmall(parent, x + 10, y, false, () -> index < images.length - 1));
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		mc.renderEngine.bindTexture(imageRes[index]);
		
		int x = GuiLexicon.PAGE_WIDTH / 2 - 53;
		int y = 6;
		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		parent.drawTexturedModalRect(x * 2 + 6, y * 2 + 6, 0, 0, 200, 200);
		GlStateManager.scale(2F, 2F, 2F);

		if(border)
			GuiLexicon.drawFromTexture(x, y, 405, 149, 106, 106);
		
		if(title != null && !title.isEmpty())
			parent.drawCenteredStringNoShadow(title, GuiLexicon.PAGE_WIDTH / 2, -3, 0x333333);
		
		if(images.length > 1 && border) {
			int xs = x + 83;
			int ys = y + 92;
			parent.drawRect(xs, ys, xs + 20, ys + 11, 0x44000000);
			parent.drawRect(xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
		}
		
		super.render(mouseX, mouseY, pticks);
	}
	
	@Override
	protected void onButtonClicked(GuiButton button) {
		if(button instanceof GuiButtonLexiconArrowSmall) {
			boolean left = ((GuiButtonLexiconArrowSmall) button).left;
			if(left)
				index--;
			else index++;
		}
	}

	@Override
	public int getTextHeight() {
		return 120;
	}
	
}
