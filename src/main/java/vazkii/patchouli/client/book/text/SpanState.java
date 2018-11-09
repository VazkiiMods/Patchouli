package vazkii.patchouli.client.book.text;

import net.minecraft.client.gui.FontRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class SpanState {
	public final GuiBook gui;
	public final Book book;
	public final int pageX;
	public final int lineHeight;
	public final FontRenderer font;
	public final int baseColor;

	public int x;
	public int y;
	public int length;
	public int color;
	public int prevColor;
	public String codes = "";
	public String tooltip = "";
	public TextClickHandler onClick = null;
	public List<Word> cluster = null;
	public boolean isExternalLink = false; // will show the "external link" symbol next to the link as soon as the link is closed
	public boolean endingExternal = false; // will show the "external link" symbol next to the link immediately

	public SpanState(GuiBook gui, Book book, int pageX, int lineHeight, int baseColor, FontRenderer font) {
		this.gui = gui;
		this.book = book;
		this.pageX = pageX;
		this.lineHeight = lineHeight;
		this.baseColor = baseColor;
		this.font = font;
	}

	public String codes(String codes) {
		this.codes = codes;
		return "";
	}

	public String color(int color) {
		this.color = color;
		return "";
	}

	public void reset() {
		endingExternal = isExternalLink;
		color = baseColor;
		codes = "";
		cluster = null;
		tooltip = "";
		onClick = null;
		isExternalLink = false;
	}
}
