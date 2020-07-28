package vazkii.patchouli.client.book.template.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentFrame extends TemplateComponent {

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if (x == -1) {
			x = GuiBook.PAGE_WIDTH / 2 - 53;
		}
		if (y == -1) {
			y = 7;
		}
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		RenderSystem.enableBlend();
		RenderSystem.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, page.book, x, y, 405, 149, 106, 106);
	}

}
