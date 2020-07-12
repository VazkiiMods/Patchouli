package vazkii.patchouli.client.book.text;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import vazkii.patchouli.api.ISpan;

import java.util.List;
import java.util.function.Supplier;

public class Span implements ISpan {

	private final String text;
	private final Color color;
	private final String codes;
	private final List<ISpan> linkCluster;
	private final ITextComponent tooltip;
	private final Supplier<Boolean> onClick;
	private final int lineBreaks;
	private final int spacingLeft;
	private final int spacingRight;
	private final boolean bold;

	public Span(SpanState state, String text) {
		this.text = text;
		this.color = state.getColor();
		this.codes = state.getCodes();
		this.onClick = state.getOnClick();
		this.linkCluster = state.getCluster();
		this.tooltip = state.getTooltip();
		this.lineBreaks = state.getLineBreaks();
		this.spacingLeft = state.getSpacingLeft();
		this.spacingRight = state.getSpacingRight();
		this.bold = getCodes().contains("\u00A7l");

		state.setLineBreaks(0);
		state.setSpacingLeft(0);
		state.setSpacingRight(0);
	}

	private Span(SpanState state, String text, Color color, String codes) {
		this.text = text;
		this.color = color;
		this.codes = codes;
		this.onClick = null;
		this.linkCluster = null;
		this.tooltip = new StringTextComponent("");
		this.lineBreaks = state.getLineBreaks();
		this.spacingLeft = state.getSpacingLeft();
		this.spacingRight = state.getSpacingRight();
		this.bold = codes.contains("\u00A7l");

		state.setLineBreaks(0);
		state.setSpacingLeft(0);
		state.setSpacingRight(0);
	}

	public static Span error(SpanState state, String message) {
		return new Span(state, message, Color.func_240743_a_(0xFF0000), "");
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getCodes() {
		return codes;
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
