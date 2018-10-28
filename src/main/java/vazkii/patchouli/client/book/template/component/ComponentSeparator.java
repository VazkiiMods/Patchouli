package vazkii.patchouli.client.book.template.component;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentSeparator extends TemplateComponent {

	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		page.parent.drawSeparator(page.book, x, y);
	}
	
}
