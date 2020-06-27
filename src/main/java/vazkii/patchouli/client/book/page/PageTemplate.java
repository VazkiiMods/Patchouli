package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.matrix.MatrixStack;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.JsonVariableWrapper;
import vazkii.patchouli.common.book.Book;

public class PageTemplate extends BookPage {

	transient BookTemplate template = null;
	transient boolean resolved = false;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);

		JsonVariableWrapper wrapper = new JsonVariableWrapper(sourceObject);

		template.compile(wrapper);
		template.build(this, entry, pageNum);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		template.onDisplayed(this, parent, left, top);
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		template.render(ms, this, mouseX, mouseY, pticks);
	}

	@Override
	public boolean func_231043_a_(double mouseX, double mouseY, int mouseButton) {
		return template.func_231043_a_(this, mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean canAdd(Book book) {
		if (!resolved) {
			template = BookTemplate.createTemplate(book, type, null);
			resolved = true;
		}

		return template != null && super.canAdd(book);
	}
}
