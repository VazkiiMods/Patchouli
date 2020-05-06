package vazkii.patchouli.api;

/**
 * Functional interface for handling macro commands
 */
@FunctionalInterface
public interface ICommandProcessor {
	/**
	 * Handles a text command and returns the text to be displayed
	 *
	 * @param  state the current span state to set styles, tooltips, etc.
	 * @return       the string that should replace the command in the text
	 */
	String process(ISpanState state);
}
