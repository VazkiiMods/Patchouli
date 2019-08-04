package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookArrowSmall;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageImage extends PageWithText {

	String images[];
	String title;
	boolean border;

	transient ResourceLocation[] imageRes;
	transient int index;
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		imageRes = new ResourceLocation[images.length];
		for(int i = 0; i < images.length; i++)
			imageRes[i] = new ResourceLocation(images[i]);
		
		int x = 90;
		int y = 100;
		addButton(new GuiButtonBookArrowSmall(parent, x, y, true, () -> index > 0, this::handleButtonArrow));
		addButton(new GuiButtonBookArrowSmall(parent, x + 10, y, false, () -> index < images.length - 1, this::handleButtonArrow));
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		mc.textureManager.bindTexture(imageRes[index]);
		
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GlStateManager.color3f(1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		parent.blit(x * 2 + 6, y * 2 + 6, 0, 0, 200, 200);
		GlStateManager.scalef(2F, 2F, 2F);

		if(border)
			GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);
		
		if(title != null && !title.isEmpty())
			parent.drawCenteredStringNoShadow(title, GuiBook.PAGE_WIDTH / 2, -3, book.headerColor);
		
		if(images.length > 1 && border) {
			int xs = x + 83;
			int ys = y + 92;
			AbstractGui.fill(xs, ys, xs + 20, ys + 11, 0x44000000);
			AbstractGui.fill(xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
		}
		
		super.render(mouseX, mouseY, pticks);
	}
	
	public void handleButtonArrow(Button button) {
		boolean left = ((GuiButtonBookArrowSmall) button).left;
		if(left)
			index--;
		else index++;
	}

	@Override
	public int getTextHeight() {
		return 120;
	}
	
}
