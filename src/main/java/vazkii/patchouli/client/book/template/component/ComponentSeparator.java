package vazkii.patchouli.client.book.template.component;

import net.minecraft.client.util.math.MatrixStack;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentSeparator extends TemplateComponent {

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if (x == -1) {
			x = 0;
		}
		if (y == -1) {
			y = 12;
		}
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		GuiBook.drawSeparator(ms, page.book, x, y);
	}

}
