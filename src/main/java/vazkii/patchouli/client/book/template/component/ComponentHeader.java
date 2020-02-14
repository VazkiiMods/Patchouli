package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.systems.RenderSystem;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.Function;

public class ComponentHeader extends TemplateComponent {

	public String text;

	@SerializedName("color")
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
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 0);
		RenderSystem.scalef(scale, scale, scale);
		
		if(centered)
			page.parent.drawCenteredStringNoShadow(page.i18n(text), 0, 0, color);
		else page.fontRenderer.draw(page.i18n(text), 0, 0, color);
		RenderSystem.popMatrix();
	}

	@Override
	public void onVariablesAvailable(Function<String, String> lookup) {
		super.onVariablesAvailable(lookup);
		text = lookup.apply(text);
		colorStr = lookup.apply(colorStr);
	}
}
