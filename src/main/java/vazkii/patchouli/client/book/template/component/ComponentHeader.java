package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.GlStateManager;

import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentHeader extends TemplateComponent {

	@VariableHolder
	public String text;
	@VariableHolder @SerializedName("color")
	public String colorStr;
	
	boolean centered = true;
	float scale = 1F;
	
	transient int color;
	
	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		try {
			color = Integer.parseInt(colorStr, 16);
		} catch(NumberFormatException e) {
			color = page.book.headerColor;
		}
		
		if(x == -1)
			x = GuiBook.PAGE_WIDTH / 2;
		if(y == -1)
			y = 0;
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(x, y, 0);
		GlStateManager.scalef(scale, scale, scale);
		
		if(centered)
			page.parent.drawCenteredStringNoShadow(text, 0, 0, color);
		else page.fontRenderer.drawString(text, 0, 0, color);
		GlStateManager.popMatrix();
	}
	
}
