package vazkii.patchouli.client.book.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import vazkii.patchouli.api.ISpan;
import vazkii.patchouli.api.ISpanState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpanState implements ISpanState {
	private final GuiBook gui;
	private final Book book;

	private final Style baseStyle;
	private final Deque<Style> styleStack = new ArrayDeque<>();
	private IFormattableTextComponent tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
	private Supplier<Boolean> onClick = null;
	private List<ISpan> cluster = null;
	private boolean isExternalLink = false; // will show the "external link" symbol next to the link as soon as the link is closed
	private boolean endingExternal = false; // will show the "external link" symbol next to the link immediately
	private int lineBreaks = 0; // force line breaks
	private int spacingLeft = 0; // add extra spacing
	private int spacingRight = 0;

	public SpanState(GuiBook gui, Book book, Style baseStyle) {
		this.gui = gui;
		this.book = book;
		this.baseStyle = baseStyle;
		this.styleStack.push(baseStyle);
	}

	@Override
	public ResourceLocation getBook() {
		return book.id;
	}

	@Override
	public Style getBaseStyle() {
		return baseStyle;
	}

	@Override
	public Deque<Style> getStyleStack() {
		return styleStack;
	}

	@Override
	public IFormattableTextComponent getTooltip() {
		return tooltip;
	}

	@Override
	public void setTooltip(IFormattableTextComponent tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public Supplier<Boolean> getOnClick() {
		return onClick;
	}

	@Override
	public String setOnClick(Supplier<Boolean> onClick) {
		this.onClick = onClick;
		return "";
	}

	@Override
	public List<ISpan> getCluster() {
		return cluster;
	}

	@Override
	public String setCluster(List<ISpan> cluster) {
		this.cluster = cluster;
		return "";
	}

	@Override
	public boolean isExternalLink() {
		return isExternalLink;
	}

	@Override
	public String setExternalLink(boolean externalLink) {
		isExternalLink = externalLink;
		return "";
	}

	@Override
	public boolean isEndingExternal() {
		return endingExternal;
	}

	@Override
	public String setEndingExternal(boolean endingExternal) {
		this.endingExternal = endingExternal;
		return "";
	}

	@Override
	public int getLineBreaks() {
		return lineBreaks;
	}

	@Override
	public String setLineBreaks(int lineBreaks) {
		this.lineBreaks = lineBreaks;
		return "";
	}

	@Override
	public int getSpacingLeft() {
		return spacingLeft;
	}

	@Override
	public String setSpacingLeft(int spacingLeft) {
		this.spacingLeft = spacingLeft;
		return "";
	}

	@Override
	public int getSpacingRight() {
		return spacingRight;
	}

	@Override
	public void setSpacingRight(int spacingRight) {
		this.spacingRight = spacingRight;
	}

	@Override
	public String color(Color color) {
		return modifyStyle(s -> s.setColor(color));
	}

	@Override
	public String baseColor() {
		return color(baseStyle.getColor());
	}

	@Override
	public String modifyStyle(Function<Style, Style> f) {
		Style top = styleStack.pop();
		styleStack.push(f.apply(top));
		return "";
	}

	@Override
	public void pushStyle(Style style) {
		Style top = styleStack.peek();
		styleStack.push(style.mergeStyle(top));
	}

	@Override
	public Style popStyle() {
		Style ret = styleStack.pop();
		if (styleStack.isEmpty()) {
			throw new IllegalStateException("Underflow in style stack");
		}
		return ret;
	}

	@Override
	public void reset() {
		endingExternal = isExternalLink;
		styleStack.clear();
		styleStack.push(baseStyle);
		cluster = null;
		tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
		onClick = null;
		isExternalLink = false;
	}

	@Override
	public Style peekStyle() {
		return styleStack.getFirst();
	}

	@Override
	public int getLinkColor() {
		return book.linkColor;
	}

	@Override
	public Minecraft getMinecraft() {
		return gui.getMinecraft();
	}
}
