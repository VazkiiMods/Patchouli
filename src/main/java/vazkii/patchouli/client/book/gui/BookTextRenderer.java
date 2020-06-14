package vazkii.patchouli.client.book.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;

import net.minecraft.client.util.math.MatrixStack;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class BookTextRenderer {
	private final Book book;
	private final GuiBook gui;
	private final String text;
	private final int x, y, width;
	private final int lineHeight;
	private final int baseColor;

	private List<Word> words;

	public BookTextRenderer(GuiBook gui, String text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}

	public BookTextRenderer(GuiBook gui, String text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		this.gui = gui;
		if (book.i18n) {
			this.text = I18n.translate(text);
		} else {
			this.text = text;
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineHeight = lineHeight;
		this.baseColor = baseColor;

		build();
	}

	private void build() {
		BookTextParser parser = new BookTextParser(gui, book, x, y, width, lineHeight, baseColor);
		words = parser.parse(text);
	}

	public void render(MatrixStack ms, int mouseX, int mouseY) {
		TextRenderer font = book.getFontStyle();
		words.forEach(word -> word.render(ms, font, mouseX, mouseY));
	}

	public boolean click(double mouseX, double mouseY, int mouseButton) {
		for (Word word : words) {
			if (word.click(mouseX, mouseY, mouseButton)) {
				return true;
			}
		}

		return false;
	}
}
