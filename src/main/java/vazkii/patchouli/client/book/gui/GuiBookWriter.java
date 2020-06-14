package vazkii.patchouli.client.book.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import vazkii.patchouli.client.book.gui.button.GuiButtonBookResize;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {

	BookTextRenderer text, editableText;
	TextFieldWidget textfield;

	private static String savedText = "";
	private static boolean drawHeader;

	public GuiBookWriter(Book book) {
		super(book, new LiteralText(""));
	}

	@Override
	public void init() {
		super.init();

		text = new BookTextRenderer(this, I18n.translate("patchouli.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
		textfield = new TextFieldWidget(textRenderer, 10, FULL_HEIGHT - 40, PAGE_WIDTH, 20, LiteralText.EMPTY);
		textfield.setMaxLength(Integer.MAX_VALUE);
		textfield.setText(savedText);

		addButton(new GuiButtonBookResize(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, false, this::handleButtonResize));
		refreshText();
	}

	@Override
	void drawForegroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(ms, mouseX, mouseY, partialTicks);

		drawCenteredStringNoShadow(ms, I18n.translate("patchouli.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
		drawSeparator(ms, book, LEFT_PAGE_X, TOP_PADDING + 12);

		if (drawHeader) {
			drawCenteredStringNoShadow(ms, I18n.translate("patchouli.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawSeparator(ms, book, RIGHT_PAGE_X, TOP_PADDING + 12);
		}

		textfield.render(ms, mouseX, mouseY, partialTicks);
		text.render(ms, mouseX, mouseY);
		editableText.render(ms, mouseX, mouseY);
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		return textfield.mouseClicked(mouseX - bookLeft, mouseY - bookTop, mouseButton)
				|| text.click(mouseX, mouseY, mouseButton)
				|| editableText.click(mouseX, mouseY, mouseButton)
				|| super.mouseClickedScaled(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (textfield.keyPressed(key, scanCode, modifiers)) {
			refreshText();
			return true;
		}

		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (textfield.charTyped(c, i)) {
			refreshText();
			return true;
		}

		return super.charTyped(c, i);
	}

	public void handleButtonResize(ButtonWidget button) {
		drawHeader = !drawHeader;
		refreshText();
	}

	public void refreshText() {
		int yPos = TOP_PADDING + (drawHeader ? 22 : -4);

		savedText = textfield.getText();
		try {
			editableText = new BookTextRenderer(this, savedText, RIGHT_PAGE_X, yPos);
		} catch (Throwable e) {
			editableText = new BookTextRenderer(this, "[ERROR]", RIGHT_PAGE_X, yPos);
			Patchouli.LOGGER.catching(e);
		}
	}
}
