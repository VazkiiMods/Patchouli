package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.vertex.PoseStack;

import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.JsonVariableWrapper;

public class PageTemplate extends BookPage {

	private transient BookTemplate template = null;
	private transient boolean resolved = false;

	@Override
	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(entry, builder, pageNum);

		if (!resolved) {
			template = BookTemplate.createTemplate(book, builder, type, null);
			resolved = true;
		}

		JsonVariableWrapper wrapper = new JsonVariableWrapper(sourceObject);

		template.compile(builder, wrapper);
		template.build(builder, this, entry, pageNum);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		template.onDisplayed(this, parent, left, top);
	}

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {
		template.render(ms, this, mouseX, mouseY, pticks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return template.mouseClicked(this, mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean overwriteKeyPressed(int keyCode, int scanCode, int modifiers) {
		return template.overwriteKeyPressed(this, keyCode, scanCode, modifiers);
	}
}
