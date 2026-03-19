package vazkii.patchouli.client.book.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {

	private @Nullable BookTextRenderer text, editableText;
	private @Nullable EditBox textfield;

	private static String savedText = "";
	private static boolean drawHeader;

	public GuiBookWriter(Book book) {
		super(book, Component.empty());
	}

	@Override
	public void init() {
		super.init();

		this.text = addRenderableOnly(new BookTextRenderer(this, Component.translatable("patchouli.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20));
		this.textfield = addRenderableWidget(new EditBox(font, 15, FULL_HEIGHT - 40, PAGE_WIDTH, 20, textfield, Component.empty()));
		this.textfield.setMaxLength(Integer.MAX_VALUE);
		if (this.textfield.getValue().isEmpty()) {
			this.textfield.setValue(savedText);
		}
		this.editableText = addRenderableOnly(new BookTextRenderer(this, Component.literal(""), RIGHT_PAGE_X, TOP_PADDING + (drawHeader ? 22 : -4)));

		addRenderableWidget(new GuiButtonBook(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, 330, 9, 11, 11, this::handleToggleHeaderButton, Component.translatable("patchouli.gui.lexicon.button.toggle_mock_header")));
		refreshText();
	}

	@Override
	void drawForegroundElements(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);

		drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
		drawSeparator(graphics, book, LEFT_PAGE_X, TOP_PADDING + 12);

		if (drawHeader) {
			drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawSeparator(graphics, book, RIGHT_PAGE_X, TOP_PADDING + 12);
		}
	}

	@Override
	public boolean mouseClickedScaled(MouseButtonEvent event, boolean doubleClick) {
		if (textfield != null && textfield.mouseClicked(new MouseButtonEvent(getRelativeX(event.x()), getRelativeY(event.y()), event.buttonInfo()), doubleClick)) {
			textfield.setFocused(true);
			return true;
		}
		if (text != null && text.click(event, doubleClick)) {
			return true;
		}
		if (editableText != null && editableText.click(event, doubleClick)) {
			return true;
		}
		return super.mouseClickedScaled(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (textfield != null && textfield.keyPressed(event)) {
			refreshText();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		if (textfield != null && textfield.charTyped(event)) {
			refreshText();
			return true;
		}

		return super.charTyped(event);
	}

	private void handleToggleHeaderButton(Button button) {
		drawHeader = !drawHeader;
		init();
	}

	private void refreshText() {
		assert textfield != null;
		assert editableText != null;
		savedText = textfield.getValue();
		try {
			editableText.setText(Component.literal(savedText));
		} catch (Throwable e) {
			editableText.setText(Component.literal("[ERROR]"));
			PatchouliAPI.LOGGER.catching(e);
		}
	}
}
