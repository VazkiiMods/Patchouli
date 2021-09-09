package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.TextLayouter;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class BookTextRenderer {
	private final Book book;
	private final GuiBook gui;
	private final Component text;
	private final int x, y, width;
	private final int lineHeight;
	private final Style baseStyle;

	private List<Word> words;
	private float scale;

	public BookTextRenderer(GuiBook gui, Component text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}

	public BookTextRenderer(GuiBook gui, Component text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		this.gui = gui;
		if (book.i18n && text instanceof TextComponent) {
			this.text = new TextComponent(I18n.get(((TextComponent) text).getText()));
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
		TextLayouter layouter = new TextLayouter(gui, x, y, lineHeight, width, PatchouliConfig.overflowMode.get());
		layouter.layout(Minecraft.getInstance().font, parser.parse(text));
		scale = layouter.getScale();
		words = layouter.getWords();
	}

	private double rescale(double in, double origin) {
		return origin + (in - origin) / scale;
	}

	public void render(PoseStack ms, int mouseX, int mouseY) {
		if (!words.isEmpty()) {
			Font font = Minecraft.getInstance().font;
			Style style = book.getFontStyle();
			Word first = words.get(0);
			ms.pushPose();
			ms.translate(first.x, first.y, 0);
			ms.scale(scale, scale, 1.0f);
			ms.translate(-first.x, -first.y, 0);
			int scaledX = (int) rescale(mouseX, first.x);
			int scaledY = (int) rescale(mouseY, first.y);
			words.forEach(word -> word.render(ms, font, style, scaledX, scaledY));
			ms.popPose();
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
