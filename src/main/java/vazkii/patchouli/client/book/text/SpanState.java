package vazkii.patchouli.client.book.text;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SpanState {
	public final GuiBook gui;
	public final Book book;

	private final Style baseStyle;
	private final Deque<Style> styleStack = new ArrayDeque<>();
	// This keeps a list of all modifications made to the style at each layer,
	// so that when we replace the base style, we can reconstruct the style stack.
	private final Deque<SpanPartialState> modifiersStack = new ArrayDeque<>();
	public IFormattableTextComponent tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
	public Supplier<Boolean> onClick = null;
	public List<Span> cluster = null;
	public boolean isExternalLink = false; // will show the "external link" symbol next to the link as soon as the link is closed
	public boolean endingExternal = false; // will show the "external link" symbol next to the link immediately
	public int lineBreaks = 0; // force line breaks
	public int spacingLeft = 0; // add extra spacing
	public int spacingRight = 0;
	public final int spaceWidth;

	public SpanState(GuiBook gui, Book book, Style baseStyle) {
		this.gui = gui;
		this.book = book;
		this.baseStyle = baseStyle;
		this.styleStack.push(baseStyle);
		this.modifiersStack.push(new SpanPartialState(null));
		this.spaceWidth = Minecraft.getInstance().fontRenderer.getStringPropertyWidth(new StringTextComponent(" ").setStyle(baseStyle));
	}

	public Style getBase() {
		return baseStyle;
	}

	public void changeBaseStyle(Style newStyle) {
		styleStack.clear();
		for (SpanPartialState state : modifiersStack) {
			newStyle = state.reroll(newStyle);
			styleStack.push(newStyle);
		}
	}

	public void color(Color color) {
		modifyStyle(s -> s.setColor(color));
	}

	public void baseColor() {
		color(baseStyle.getColor());
	}

	public void modifyStyle(UnaryOperator<Style> f) {
		Style top = styleStack.pop();
		styleStack.push(f.apply(top));
		modifiersStack.peek().addModification(f);
	}

	public void pushStyle(Style style) {
		Style top = styleStack.peek();
		modifiersStack.push(new SpanPartialState(style));
		styleStack.push(style.mergeStyle(top));
	}

	public Style popStyle() {
		if (styleStack.size() <= 1) {
			throw new IllegalStateException("Underflow in style stack");
		}
		Style ret = styleStack.pop();
		modifiersStack.pop();
		return ret;
	}

	public void reset() {
		endingExternal = isExternalLink;
		styleStack.clear();
		styleStack.push(baseStyle);
		modifiersStack.clear();
		modifiersStack.push(new SpanPartialState(null));
		cluster = null;
		tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
		onClick = null;
		isExternalLink = false;
	}

	public Style peekStyle() {
		return styleStack.getFirst();
	}

	// Represents the transformations on a single stack frame.
	private class SpanPartialState {
		// This is null iff this state represents the base state (i.e. nothing to merge)
		private final Style mergeStyle;
		// List of all the transformations that are applied to this frame.
		private List<UnaryOperator<Style>> transformations = null;

		public SpanPartialState(Style style) {
			this.mergeStyle = style;
		}

		public void addModification(UnaryOperator<Style> f) {
			if (transformations == null) {
				// We use a linked list because this list is likely to be very small
				// in order to skimp on space.
				transformations = new LinkedList<>();
			}
			transformations.add(f);
		}

		public Style reroll(Style style) {
			if (mergeStyle != null) {
				style = mergeStyle.mergeStyle(style);
			}
			if (transformations != null) {
				for (UnaryOperator<Style> f : transformations) {
					style = f.apply(style);
				}
			}
			return style;
		}
	}
}
