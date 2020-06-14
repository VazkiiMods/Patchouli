package vazkii.patchouli.client.book.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpanState {
	public final GuiBook gui;
	public final Book book;

	private final Style baseStyle;
	private final Deque<Style> styleStack = new ArrayDeque<>();
	public MutableText tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
	public Supplier<Boolean> onClick = null;
	public List<Span> cluster = null;
	public boolean isExternalLink = false; // will show the "external link" symbol next to the link as soon as the link is closed
	public boolean endingExternal = false; // will show the "external link" symbol next to the link immediately
	public int lineBreaks = 0; // force line breaks
	public int spacingLeft = 0; // add extra spacing
	public int spacingRight = 0;

	public SpanState(GuiBook gui, Book book, Style baseStyle) {
		this.gui = gui;
		this.book = book;
		this.baseStyle = baseStyle;
		this.styleStack.push(baseStyle);
	}

	public String color(TextColor color) {
		return modifyStyle(s -> s.withColor(color));
	}

	public String baseColor() {
		return color(baseStyle.getColor());
	}

	public String modifyStyle(Function<Style, Style> f) {
		Style top = styleStack.pop();
		styleStack.push(f.apply(top));
		return "";
	}

	public void pushStyle(Style style) {
		Style top = styleStack.peek();
		styleStack.push(style.withParent(top));
	}

	public Style popStyle() {
		Style ret = styleStack.pop();
		if (styleStack.isEmpty()) {
			throw new IllegalStateException("Underflow in style stack");
		}
		return ret;
	}

	public void reset() {
		endingExternal = isExternalLink;
		styleStack.clear();
		styleStack.push(baseStyle);
		cluster = null;
		tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
		onClick = null;
		isExternalLink = false;
	}

	public Style peekStyle() {
		return styleStack.getFirst();
	}
}
