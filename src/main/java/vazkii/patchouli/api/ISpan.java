package vazkii.patchouli.api;

import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Supplier;

public interface ISpan {
	String getText();

	int getColor();

	String getCodes();

	List<ISpan> getLinkCluster();

	ITextComponent getTooltip();

	Supplier<Boolean> getOnClick();

	int getLineBreaks();

	int getSpacingLeft();

	int getSpacingRight();

	boolean isBold();
}
