package vazkii.patchouli.client.book.text;

@FunctionalInterface
public interface TextFunctionHandler {
	String process(String parameter, SpanState state);
}
