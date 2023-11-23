package vazkii.patchouli.client.book.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {

	private BookTextRenderer text, editableText;
	private EditBox textfield;

	private static String savedText = "";
	private static boolean drawHeader;

	public GuiBookWriter(Book book) {
		super(book, Component.empty());
	}

	@Override
	public void init() {
		super.init();

		text = new BookTextRenderer(this, Component.translatable("patchouli.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
		textfield = new EditBox(font, 10, FULL_HEIGHT - 40, PAGE_WIDTH, 20, Component.empty());
		textfield.setMaxLength(Integer.MAX_VALUE);
		textfield.setValue(savedText);

		var button = new GuiButtonBook(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, 330, 9, 11, 11, this::handleToggleHeaderButton,
				Component.translatable("patchouli.gui.lexicon.button.toggle_mock_header"));
		addRenderableWidget(button);
		refreshText();
	}

	@Override
	void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);

		drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
		drawSeparator(graphics, book, LEFT_PAGE_X, TOP_PADDING + 12);

		if (drawHeader) {
			drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawSeparator(graphics, book, RIGHT_PAGE_X, TOP_PADDING + 12);
		}

		textfield.render(graphics, mouseX, mouseY, partialTicks);
		text.render(graphics, mouseX, mouseY);
		editableText.render(graphics, mouseX, mouseY);
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		return textfield.mouseClicked(getRelativeX(mouseX), getRelativeY(mouseY), mouseButton)
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

	private void handleToggleHeaderButton(Button button) {
		drawHeader = !drawHeader;
		refreshText();
	}

	private void refreshText() {
		int yPos = TOP_PADDING + (drawHeader ? 22 : -4);

		savedText = textfield.getValue();
		try {
			editableText = new BookTextRenderer(this, Component.literal(savedText), RIGHT_PAGE_X, yPos);
		} catch (Throwable e) {
			editableText = new BookTextRenderer(this, Component.literal("[ERROR]"), RIGHT_PAGE_X, yPos);
			PatchouliAPI.LOGGER.catching(e);
		}
	}
}
