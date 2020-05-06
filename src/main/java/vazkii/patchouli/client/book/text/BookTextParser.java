package vazkii.patchouli.client.book.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.MacroRegistry;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BookTextParser {
	public static final StringTextComponent EMPTY_STRING_COMPONENT = new StringTextComponent("");

	private final GuiBook gui;
	private final Book book;
	private final int x, y, width;
	private final int lineHeight;
	private final int baseColor;
	private final FontRenderer font;
	private final int spaceWidth;

	public BookTextParser(GuiBook gui, Book book, int x, int y, int width, int lineHeight, int baseColor) {
		this.gui = gui;
		this.book = book;
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineHeight = lineHeight;
		this.baseColor = baseColor;

		this.font = book.getFont();
		this.spaceWidth = font.getStringWidth(" ");
	}

	public List<Word> parse(@Nullable String text) {
		String actualText = text;
		if (actualText == null) {
			actualText = "[ERROR]";
		}

		int i = 0;
		int expansionCap = 10;
		for (; i < expansionCap; i++) {
			String newText = actualText;
			for (Map.Entry<String, String> e : book.macros.entrySet()) {
				newText = newText.replace(e.getKey(), e.getValue());
			}

			if (newText.equals(actualText)) {
				break;
			} else {
				actualText = newText;
			}
		}

		if (i == expansionCap) {
			Patchouli.LOGGER.warn("Expanded macros for {} iterations without reaching fixpoint, stopping. " +
					"Make sure you don't have circular macro invocations", expansionCap);
		}

		List<Span> spans = processCommands(actualText);
		List<Word> words = layout(spans);

		return words;
	}

	private List<Word> layout(List<Span> spans) {
		TextLayouter layouter = new TextLayouter(gui, x, y, lineHeight, width);
		layouter.layout(font, spans);
		return layouter.getWords();
	}

	private List<Span> processCommands(String text) {
		SpanState state = new SpanState(gui, book, baseColor, font);
		List<Span> spans = new ArrayList<>();

		int from = 0;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '$' && i + 1 < chars.length && chars[i + 1] == '(') {
				if (i > from) {
					spans.add(new Span(state, text.substring(from, i)));
				}

				from = i;
				while (i < chars.length && chars[i] != ')') {
					i++;
				}

				if (i == chars.length) {
					spans.add(Span.error(state, "[ERROR: UNFINISHED COMMAND]"));
					break;
				}

				try {
					String processed = processCommand(state, text.substring(from + 2, i));
					if (!processed.isEmpty()) {
						spans.add(new Span(state, processed));

						if (state.cluster == null) {
							state.tooltip = EMPTY_STRING_COMPONENT;
						}
					}
				} catch (Exception ex) {
					spans.add(Span.error(state, "[ERROR]"));
				}

				from = i + 1;
			}
		}
		spans.add(new Span(state, text.substring(from)));
		return spans;
	}

	private String processCommand(SpanState state, String cmd) {
		state.endingExternal = false;
		String result = "";

		if (cmd.length() == 1 && cmd.matches("^[0123456789abcdef]$")) { // Vanilla colors
			state.color = TextFormatting.fromFormattingCode(cmd.charAt(0)).getColor();
			return "";
		} else if (cmd.startsWith("#") && (cmd.length() == 4 || cmd.length() == 7)) { // Hex colors
			String parse = cmd.substring(1);
			if (parse.length() == 3) {
				parse = "" + parse.charAt(0) + parse.charAt(0) + parse.charAt(1) + parse.charAt(1) + parse.charAt(2) + parse.charAt(2);
			}
			try {
				state.color = Integer.parseInt(parse, 16);
			} catch (NumberFormatException e) {
				state.color = baseColor;
			}
			return "";
		} else if (cmd.matches("li\\d?")) { // List Element
			char c = cmd.length() > 2 ? cmd.charAt(2) : '1';
			int dist = Character.isDigit(c) ? Character.digit(c, 10) : 1;
			int pad = dist * 4;
			char bullet = dist % 2 == 0 ? '\u25E6' : '\u2022';
			state.lineBreaks = 1;
			state.spacingLeft = pad;
			state.spacingRight = spaceWidth;
			return TextFormatting.BLACK.toString() + bullet;
		}

		if (cmd.indexOf(':') > 0) {
			int index = cmd.indexOf(':');
			String function = cmd.substring(0, index);
			String parameter = cmd.substring(index + 1);
			if (MacroRegistry.INSTANCE.functionExists(function)) {
				result = MacroRegistry.INSTANCE.processFunction(function, parameter, state);
			} else {
				result = "[MISSING FUNCTION: " + function + "]";
			}
		} else if (MacroRegistry.INSTANCE.commandExists(cmd)) {
			result = MacroRegistry.INSTANCE.processCommand(cmd, state);
		}

		if (state.endingExternal) {
			result += TextFormatting.GRAY + "\u21AA";
		}

		return result;
	}
}
