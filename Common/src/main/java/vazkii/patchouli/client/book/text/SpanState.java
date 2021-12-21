package vazkii.patchouli.client.book.text;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import vazkii.patchouli.api.IStyleStack;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SpanState implements IStyleStack {
	public final GuiBook gui;
	public final Book book;

	private Style baseStyle;
	// In addition to caching the computed style for quick lookup,
	// this keeps a list of all modifications made to the style at each layer,
	// so that when we replace the base style, we can reconstruct the style stack.
	private final Deque<SpanPartialState> stateStack = new ArrayDeque<>();
	public MutableComponent tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
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
		this.stateStack.push(new SpanPartialState(baseStyle, null));
		this.spaceWidth = Minecraft.getInstance().font.width(new TextComponent(" ").setStyle(baseStyle));
	}

	public Style getBase() {
		return baseStyle;
	}

	public void changeBaseStyle(Style newStyle) {
		baseStyle = newStyle;
		for (SpanPartialState state : stateStack) {
			state.replaceBase(newStyle);
			newStyle = state.getCurrentStyle();
		}
	}

	public void color(TextColor color) {
		modifyStyle(s -> s.withColor(color));
	}

	public void baseColor() {
		color(baseStyle.getColor());
	}

	@Override
	public void modifyStyle(UnaryOperator<Style> f) {
		stateStack.peek().addModification(f);
	}

	@Override
	public void pushStyle(Style style) {
		stateStack.push(new SpanPartialState(style.applyTo(peekStyle()), style));
	}

	@Override
	public Style popStyle() {
		if (stateStack.size() <= 1) {
			throw new IllegalStateException("Underflow in style stack");
		}
		return stateStack.pop().getCurrentStyle();
	}

	@Override
	public void reset() {
		endingExternal = isExternalLink;
		stateStack.clear();
		stateStack.push(new SpanPartialState(baseStyle, null));
		cluster = null;
		tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
		onClick = null;
		isExternalLink = false;
	}

	@Override
	public Style peekStyle() {
		return stateStack.peek().getCurrentStyle();
	}

	// Represents the styling applied to a single stack frame.
	private static class SpanPartialState {
		// This is the current style of the frame.
		private Style currentStyle;
		// This is null iff this frame represents the base state (i.e. nothing to merge),
		@Nullable private final Style mergeStyle;
		// List of all the transformations that are applied to this frame.
		@Nullable private List<UnaryOperator<Style>> transformations = null;

		// Takes the current style state from downstream
		// and a style to merge onto it.
		public SpanPartialState(Style currentStyle, Style mergeStyle) {
			this.currentStyle = currentStyle;
			this.mergeStyle = mergeStyle;
		}

		public Style getCurrentStyle() {
			return currentStyle;
		}

		public void addModification(UnaryOperator<Style> f) {
			if (transformations == null) {
				// We use a linked list because this list is likely to be very small
				// in order to skimp on space.
				transformations = new LinkedList<>();
			}
			transformations.add(f);
			this.currentStyle = f.apply(currentStyle);
		}

		public void replaceBase(Style style) {
			if (mergeStyle != null) {
				style = mergeStyle.applyTo(style);
			}
			if (transformations != null) {
				for (UnaryOperator<Style> f : transformations) {
					style = f.apply(style);
				}
			}
			this.currentStyle = style;
		}
	}
}
