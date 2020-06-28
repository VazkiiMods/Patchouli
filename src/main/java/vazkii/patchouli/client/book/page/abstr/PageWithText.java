package vazkii.patchouli.client.book.page.abstr;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public abstract class PageWithText extends BookPage {

	IVariable text;

	transient BookTextRenderer textRender;

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if (text == null) {
			text = IVariable.wrap("");
		}

		textRender = new BookTextRenderer(parent, text.as(ITextComponent.class).getString(), 0, getTextHeight());
	}

	public abstract int getTextHeight();

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		if (shouldRenderText()) {
			textRender.render(ms, mouseX, mouseY);
		}
	}

	@Override
	public boolean func_231043_a_(double mouseX, double mouseY, int mouseButton) {
		return shouldRenderText() && textRender.click(mouseX, mouseY, mouseButton);
	}

	public boolean shouldRenderText() {
		return true;
	}

}
