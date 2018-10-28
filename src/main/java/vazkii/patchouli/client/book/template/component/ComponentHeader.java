package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.client.book.template.TemplateComponent.VariableHolder;

public class ComponentHeader extends TemplateComponent {

	@VariableHolder
	public String text;
	@VariableHolder @SerializedName("color")
	public String colorStr;
	
	public boolean centered = true;
	
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
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		if(centered)
			page.parent.drawCenteredStringNoShadow(text, x, y, color);
		else page.fontRenderer.drawString(text, x, y, color);
	}
	
}
