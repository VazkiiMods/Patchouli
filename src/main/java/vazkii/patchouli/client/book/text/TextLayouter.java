package vazkii.patchouli.client.book.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.MinecraftForgeClient;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TextLayouter} is responsible for taking a series of {@link Span spans}
 * and computing its layout and positioning, producing a collection of {@link Word words}.
 */
public class TextLayouter {
	private final List<Word> words = new ArrayList<>();
	private final GuiBook gui;
	private final int pageX;
	private final int lineHeight;
	private final int pageWidth;

	public TextLayouter(GuiBook gui, int pageX, int pageY, int lineHeight, int pageWidth) {
		this.gui = gui;
		this.pageX = pageX;
		this.lineHeight = lineHeight;
		this.pageWidth = pageWidth;

		y = pageY;
	}

	private int y;
	private List<Word> linkCluster = null;
	private List<Span> spanCluster = null;

	private final List<SpanTail> pending = new ArrayList<>();
	private int lineStart = 0;
	private int widthSoFar = 0;

	private FontRenderer font;

	public void layout(FontRenderer font, List<Span> spans) {
		this.font = font;

		List<Span> paragraph = new ArrayList<>();
		for (Span span : spans) {
			if (span.lineBreaks > 0) {
				layoutParagraph(paragraph);

				widthSoFar = 0;
				y += span.lineBreaks * lineHeight;
				paragraph.clear();
			}

			paragraph.add(span);
		}
		if (!paragraph.isEmpty()) {
			layoutParagraph(paragraph);
		}
	}

	// a paragraph is a series of spans without explicit line break
	private void layoutParagraph(List<Span> paragraph) {
		String text = toString(paragraph);
		BreakIterator iterator = BreakIterator.getLineInstance(MinecraftForgeClient.getLocale());
		iterator.setText(text);
		lineStart = 0;

		for (Span span : paragraph) {
			layoutSpan(iterator, span);
		}

		flush();
	}

	private void layoutSpan(BreakIterator iterator, Span span) {
		if (spanCluster != span.linkCluster) {
			linkCluster = span.linkCluster == null ? null : new ArrayList<>();
			spanCluster = span.linkCluster;
		}

		SpanTail last = new SpanTail(span, 0, linkCluster);
		widthSoFar += last.width;
		pending.add(last);

		while (widthSoFar > pageWidth) {
			breakLine(iterator);

			widthSoFar = 0;
			for (SpanTail pending : this.pending) {
				widthSoFar += pending.width;
			}
		}
	}

	private void breakLine(BreakIterator iterator) {
		int width = 0;
		int offset = 0;
		for (SpanTail pending : this.pending) {
			width += pending.width;
			offset += pending.length;
		}

		SpanTail last = pending.get(pending.size() - 1);
		width -= last.width;
		offset -= last.length;

		char[] characters = last.span.text.toCharArray();
		for (int i = last.start; i < characters.length; i++) {
			ITextComponent tmp = new StringTextComponent(String.valueOf(characters[i])).setStyle(last.span.style);
			width += font.getStringPropertyWidth(tmp);
			if (last.span.bold) {
				width++;
			}

			if (width > pageWidth) {
				int overflowOffset = lineStart + offset + i - last.start;
				int breakOffset = overflowOffset + 1;
				if (!Character.isWhitespace(characters[i])) {
					breakOffset = iterator.preceding(breakOffset);
				}
				if (breakOffset <= lineStart) { // could not break: we have a long word
					breakOffset = overflowOffset - 1; // cut off the word
				}

				breakLine(breakOffset);
				return;
			}
		}

		// if this point has been reached, it means getCharWidth and getStringWidth give conflicting results
		// so just break the line here; to be sure the book doesn't hang the client in an infinite loop
		// it shouldn't happen [but you never know]
		flush();
		y += lineHeight;
	}

	private String toString(List<Span> paragraph) {
		StringBuilder result = new StringBuilder();
		for (Span span : paragraph) {
			result.append(span.text);
		}
		return result.toString();
	}

	public void flush() {
		if (pending.isEmpty()) {
			return;
		}

		int x = pageX;
		for (SpanTail pending : this.pending) {
			words.add(pending.position(gui, x, y, pending.length));
			x += pending.width;
		}
		pending.clear();
	}

	private void breakLine(int textOffset) {
		int index;
		int offset = lineStart;
		int x = pageX;
		for (index = 0; index < pending.size(); index++) {
			SpanTail span = pending.get(index);
			if (offset + span.length < textOffset) {
				words.add(span.position(gui, x, y, span.length));
				offset += span.length;
				x += span.width;
			} else {
				words.add(span.position(gui, x, y, textOffset - offset));
				pending.set(index, span.tail(textOffset - offset));
				break;
			}
		}
		for (int i = index - 1; i >= 0; i--) {
			pending.remove(i);
		}

		lineStart = textOffset;
		y += lineHeight;
	}

	public List<Word> getWords() {
		return words;
	}

	private class SpanTail {

		private final Span span;
		private final int start;
		private final int width;
		private final List<Word> cluster;
		private final int length;

		public SpanTail(Span span, int start, List<Word> cluster) {
			this.span = span;
			this.start = start;
			this.width = font.getStringPropertyWidth(span.styledSubstring(start)) + span.spacingLeft + span.spacingRight;
			this.cluster = cluster;
			this.length = span.text.length() - start;
		}

		public Word position(GuiBook gui, int x, int y, int length) {
			x += span.spacingLeft;
			Word result = new Word(gui, span, span.styledSubstring(start, start + length), x, y, width, cluster);
			if (cluster != null) {
				cluster.add(result);
			}
			return result;
		}

		public SpanTail tail(int offset) {
			return new SpanTail(span, start + offset, cluster);
		}
	}
}
