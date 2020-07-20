package vazkii.patchouli.client.book.text;

import net.minecraft.util.text.*;

import vazkii.patchouli.api.ISpan;

import java.util.List;
import java.util.function.Supplier;

public class Span implements ISpan {
	public static Span error(SpanState state, String message) {
		return new Span(state, message, Style.EMPTY.applyFormatting(TextFormatting.RED));
	}

	private final String text;
	private final Style style;
	private final List<ISpan> linkCluster;
	private final ITextComponent tooltip;
	private final Supplier<Boolean> onClick;
	private final int lineBreaks;
	private final int spacingLeft;
	private final int spacingRight;
	private final boolean bold;

	public Span(SpanState state, String text) {
		this.text = text;
		this.style = state.peekStyle();
		this.onClick = state.getOnClick();
		this.linkCluster = state.getCluster();
		this.tooltip = state.getTooltip();
		this.lineBreaks = state.getLineBreaks();
		this.spacingLeft = state.getSpacingLeft();
		this.spacingRight = state.getSpacingRight();
		this.bold = style.getBold();

		state.setLineBreaks(0);
		state.setSpacingLeft(0);
		state.setSpacingRight(0);
	}

	private Span(SpanState state, String text, Style style) {
		this.text = text;
		this.style = style;
		this.onClick = null;
		this.linkCluster = null;
		this.tooltip = new StringTextComponent("");
		this.lineBreaks = state.getLineBreaks();
		this.spacingLeft = state.getSpacingLeft();
		this.spacingRight = state.getSpacingRight();
		this.bold = style.getBold();

		state.setLineBreaks(0);
		state.setSpacingLeft(0);
		state.setSpacingRight(0);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Style getStyle() {
		return style;
	}

	@Override
	public List<ISpan> getLinkCluster() {
		return linkCluster;
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
	public boolean isBold() {
		return bold;
	}
}
