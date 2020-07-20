package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentHeader extends TemplateComponent {

	public IVariable text;

	@SerializedName("color") public IVariable colorStr;

	boolean centered = true;
	float scale = 1F;

	transient ITextComponent actualText;
	transient int color;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		try {
			color = Integer.parseInt(colorStr.asString(""), 16);
		} catch (NumberFormatException e) {
			color = page.book.headerColor;
		}

		if (x == -1) {
			x = GuiBook.PAGE_WIDTH / 2;
		}
		if (y == -1) {
			y = 0;
		}
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		ms.push();
		ms.translate(x, y, 0);
		ms.scale(scale, scale, scale);

		if (centered) {
			page.parent.drawCenteredStringNoShadow(ms, page.i18n(actualText.getString()), 0, 0, color);
		} else {
			page.fontRenderer.drawString(ms, page.i18n(actualText.getString()), 0, 0, color);
		}
		ms.pop();
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		actualText = lookup.apply(text).as(ITextComponent.class);
		colorStr = lookup.apply(colorStr);
	}
}
