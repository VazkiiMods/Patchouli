package vazkii.patchouli.api;

import net.minecraft.util.text.Style;

import java.util.function.UnaryOperator;

/**
 * Represents a style (or style stack) when rendering book text.
 */
public interface IRenderingStyle {
	/** Modify the style at the top of the stack. */
	void modifyStyle(UnaryOperator<Style> f);

	/** Push a new style onto the style stack. */
	void pushStyle(Style style);

	/** Pop the last style off the stack. Throws if called more times than pushStyle() was called. */
	Style popStyle();

	/** Peek at the current style on the stack. */
	Style peekStyle();

	/** Reset global style state. */
	void reset();
}
