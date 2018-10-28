package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentText extends TemplateComponent {

	@VariableHolder
	String text;
	
	int maxWidth = GuiBook.PAGE_WIDTH;
	int lineHeight = GuiBook.TEXT_LINE_HEIGHT;
	
	transient BookTextRenderer textRenderer;
	
	@Override
	public void build(BookPage page, GuiBookEntry parent, int pageNum) {
		textRenderer = new BookTextRenderer(parent, text, x, y, maxWidth, lineHeight);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		textRenderer.render(mouseX, mouseY);
	}
	
	@Override
	public void mouseClicked(BookPage page, int mouseX, int mouseY, int mouseButton) {
		textRenderer.click(mouseX, mouseY, mouseButton);
	}

}
