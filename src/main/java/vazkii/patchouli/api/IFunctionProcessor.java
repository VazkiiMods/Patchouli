package vazkii.patchouli.api;

/**
 * Functional interface for handling macro functions
 */
@FunctionalInterface
public interface IFunctionProcessor {
	/**
	 * Handles a text function and returns the text to be displayed
	 *
	 * @param  parameter the function parameter
	 * @param  state     the current span state to set styles, tooltips, etc.
	 * @return           the text to be displayed
	 */
	String process(String parameter, ISpanState state);
}
