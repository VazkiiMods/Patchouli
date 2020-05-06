package vazkii.patchouli.api;

import vazkii.patchouli.client.book.text.SpanState;

/**
 * Functional interface for handling macro commands
 */
@FunctionalInterface
public interface ICommandProcessor {
	String process(SpanState state);
}
