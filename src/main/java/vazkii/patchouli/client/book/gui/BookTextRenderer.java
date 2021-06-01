package vazkii.patchouli.client.book.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.TextLayouter;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class BookTextRenderer {
	private final Book book;
	private final GuiBook gui;
	private final Text text;
	private final int x, y, width;
	private final int lineHeight;
	private final Style baseStyle;

	private List<Word> words;
	private float scale;

	public BookTextRenderer(GuiBook gui, Text text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}

	public BookTextRenderer(GuiBook gui, Text text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		this.gui = gui;
		if (book.i18n && text instanceof LiteralText) {
			this.text = new LiteralText(I18n.translate(((LiteralText) text).getRawString()));
		} else {
			this.text = text;
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineHeight = lineHeight;
		this.baseStyle = book.getFontStyle().withColor(TextColor.fromRgb(baseColor));

		build();
	}

	private void build() {
		BookTextParser parser = new BookTextParser(gui, book, x, y, width, lineHeight, baseStyle);
		TextLayouter layouter = new TextLayouter(gui, x, y, lineHeight, width, PatchouliConfig.overflowMode.getValue());
		layouter.layout(MinecraftClient.getInstance().textRenderer, parser.parse(text));
		scale = layouter.getScale();
		words = layouter.getWords();
	}

	private double rescale(double in, double origin) {
		return origin + (in - origin) / scale;
	}

	public void render(MatrixStack ms, int mouseX, int mouseY) {
		if (!words.isEmpty()) {
			TextRenderer font = MinecraftClient.getInstance().textRenderer;
			Style style = book.getFontStyle();
			Word first = words.get(0);
			ms.push();
			ms.translate(first.x, first.y, 0);
			ms.scale(scale, scale, 1.0f);
			ms.translate(-first.x, -first.y, 0);
			int scaledX = (int) rescale(mouseX, first.x);
			int scaledY = (int) rescale(mouseY, first.y);
			words.forEach(word -> word.render(ms, font, style, scaledX, scaledY));
			ms.pop();
		}
	}

	public boolean click(double mouseX, double mouseY, int mouseButton) {
		if (!words.isEmpty()) {
			Word first = words.get(0);
			double scaledX = rescale(mouseX, first.x);
			double scaledY = rescale(mouseY, first.y);
			for (Word word : words) {
				if (word.click(scaledX, scaledY, mouseButton)) {
					return true;
				}
			}
		}

		return false;
	}
}
