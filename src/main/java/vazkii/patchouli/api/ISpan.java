package vazkii.patchouli.api;

import net.minecraft.util.text.*;

import java.util.List;
import java.util.function.Supplier;

public interface ISpan {

	String getText();

	Style getStyle();

	List<ISpan> getLinkCluster();

	ITextComponent getTooltip();

	Supplier<Boolean> getOnClick();

	int getLineBreaks();

	int getSpacingLeft();

	int getSpacingRight();

	boolean isBold();

	default IFormattableTextComponent styledSubstring(int start) {
		return new StringTextComponent(getText().substring(start)).func_230530_a_(getStyle());
	}

	default IFormattableTextComponent styledSubstring(int start, int end) {
		return new StringTextComponent(getText().substring(start, end)).func_230530_a_(getStyle());
	}
}
