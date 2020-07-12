package vazkii.patchouli.client.book.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.ISpan;
import vazkii.patchouli.api.ISpanState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SpanState implements ISpanState {
	private final GuiBook gui;
	private final Book book;
	private final FontRenderer font;
	private final int baseColor;

	private Color color;
	private Color prevColor;
	private String codes = "";
	private ITextComponent tooltip = BookTextParser.EMPTY_STRING_COMPONENT;
	private Supplier<Boolean> onClick = null;
	private List<ISpan> cluster = null;
	private boolean isExternalLink = false; // will show the "external link" symbol next to the link as soon as the link is closed
	private boolean endingExternal = false; // will show the "external link" symbol next to the link immediately
	private int lineBreaks = 0; // force line breaks
	private int spacingLeft = 0; // add extra spacing
	private int spacingRight = 0;

	public SpanState(GuiBook gui, Book book, int baseColor, FontRenderer font) {
		this.gui = gui;
		this.book = book;
		this.baseColor = baseColor;
		this.font = font;

		this.setColor(baseColor);
		this.setPrevColor(baseColor);
	}

	/*@Override
	public GuiBook getGui() {
		return gui;
	}*/

	@Override
	public ResourceLocation getBook() {
		return book.id;
	}

	@Override
	public FontRenderer getFont() {
		return font;
	}

	@Override
	public int getBaseColor() {
		return baseColor;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Color getPrevColor() {
		return prevColor;
	}

	@Override
	public String getCodes() {
		return codes;
	}

	@Override
	public ITextComponent getTooltip() {
		return tooltip;
	}

	@Override
	public Supplier<Boolean> getOnClick() {
		return onClick;
	}

	@Override
	public List<ISpan> getCluster() {
		return cluster;
	}

	@Override
	public boolean isExternalLink() {
		return isExternalLink;
	}

	@Override
	public boolean isEndingExternal() {
		return endingExternal;
	}

	@Override
	public int getLineBreaks() {
		return lineBreaks;
	}

	@Override
	public int getSpacingLeft() {
		return spacingLeft;
	}

	@Override
	public int getSpacingRight() {
		return spacingRight;
	}

	@Override
	public int getLinkColor() {
		return this.book.linkColor;
	}

	@Override
	public Minecraft getMinecraft() {
		return this.gui.getMinecraft();
	}

	@Override
	public String setCodes(String codes) {
		this.codes = codes;
		return "";
	}

	@Override
	public String setColor(Color color) {
		this.color = color;
		return "";
	}

	@Override
	public String setColor(int color) {
		this.color = Color.func_240743_a_(color);
		return "";
	}

	@Override
	public String setPrevColor(Color prevColor) {
		this.prevColor = prevColor;
		return "";
	}

	@Override
	public String setPrevColor(int prevColor) {
		this.prevColor = Color.func_240743_a_(prevColor);
		return "";
	}

	@Override
	public String setTooltip(ITextComponent tooltip) {
		this.tooltip = tooltip;
		return "";
	}

	@Override
	public String setOnClick(@Nullable Supplier<Boolean> onClick) {
		this.onClick = onClick;
		return "";
	}

	@Override
	public String setCluster(@Nullable List<ISpan> cluster) {
		this.cluster = cluster;
		return "";
	}

	@Override
	public String setExternalLink(boolean externalLink) {
		isExternalLink = externalLink;
		return "";
	}

	@Override
	public String setEndingExternal(boolean endingExternal) {
		this.endingExternal = endingExternal;
		return "";
	}

	@Override
	public String setLineBreaks(int lineBreaks) {
		this.lineBreaks = lineBreaks;
		return "";
	}

	@Override
	public String setSpacingLeft(int spacingLeft) {
		this.spacingLeft = spacingLeft;
		return "";
	}

	@Override
	public String setSpacingRight(int spacingRight) {
		this.spacingRight = spacingRight;
		return "";
	}

	@Override
	public void reset() {
		setEndingExternal(isExternalLink());
		setColor(getBaseColor());
		setCodes("");
		setCluster(null);
		setTooltip(BookTextParser.EMPTY_STRING_COMPONENT);
		setOnClick(null);
		setExternalLink(false);
	}
}
