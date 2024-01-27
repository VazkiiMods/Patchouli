package vazkii.patchouli.client.book.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;

import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.TextLayouter;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class BookTextRenderer {
	private final Book book;

	private final List<Word> words;
	private final float scale;

	public BookTextRenderer(GuiBook gui, Component text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}

	public BookTextRenderer(GuiBook gui, Component text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		Component text1;
		if (book.i18n && text.getContents() instanceof PlainTextContents.LiteralContents lc) {
			text1 = Component.literal(I18n.get(lc.text()));
		} else {
			text1 = text;
		}
		Style baseStyle = book.getFontStyle().withColor(TextColor.fromRgb(baseColor));

		var parser = new BookTextParser(gui, this.book, x, y, width, lineHeight, baseStyle);
		var overflowMode = this.book.overflowMode;
		if (overflowMode == null) {
			overflowMode = PatchouliConfig.get().overflowMode();
		}
		var layouter = new TextLayouter(gui, x, y, lineHeight, width, overflowMode);
		layouter.layout(Minecraft.getInstance().font, parser.parse(text1));
		this.scale = layouter.getScale();
		this.words = layouter.getWords();
	}

	private double rescale(double in, double origin) {
		return origin + (in - origin) / scale;
	}

	public void render(GuiGraphics graphics, int mouseX, int mouseY) {
		if (!words.isEmpty()) {
			Font font = Minecraft.getInstance().font;
			Style style = book.getFontStyle();
			Word first = words.get(0);
			graphics.pose().pushPose();
			graphics.pose().translate(first.x, first.y, 0);
			graphics.pose().scale(scale, scale, 1.0f);
			graphics.pose().translate(-first.x, -first.y, 0);
			int scaledX = (int) rescale(mouseX, first.x);
			int scaledY = (int) rescale(mouseY, first.y);
			words.forEach(word -> word.render(graphics, font, style, scaledX, scaledY));
			graphics.pose().popPose();
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
