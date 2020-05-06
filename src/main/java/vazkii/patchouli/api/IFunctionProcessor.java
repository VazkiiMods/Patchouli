package vazkii.patchouli.api;

import vazkii.patchouli.client.book.text.SpanState;

/**
 * Functional interface for handling macro functions
 */
@FunctionalInterface
public interface IFunctionProcessor {
	String process(String parameter, SpanState state);
}
