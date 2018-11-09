package vazkii.patchouli.client.book.text;

@FunctionalInterface
public interface TextCommandHandler {
	String process(SpanState state);
}
