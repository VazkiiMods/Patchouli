package vazkii.patchouli.client.book.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.book.Book;

public class BookTextParser {
	private static final Map<String, CommandProcessor> COMMANDS = new HashMap<>();
	private static final Map<String, FunctionProcessor> FUNCTIONS = new HashMap<>();

	private static void register(CommandProcessor handler, String... names) {
		for (String name : names)
			COMMANDS.put(name, handler);
	}

	static {
		register(state -> {
			state.length = 0;
			state.x = state.pageX;
			state.y += state.lineHeight;
			return "";
		}, "br");
		register(state -> {
			state.length = 0;
			state.x = state.pageX;
			state.y += state.lineHeight * 2;
			return "";
		}, "br2", "2br", "p");
		register(state -> {
			state.endingExternal = state.isExternalLink;
			state.color = state.prevColor;
			state.cluster = null;
			state.tooltip = "";
			state.onClick = null;
			state.isExternalLink = false;
			return "";
		}, "/l");
		register(state -> {
			state.cluster = null;
			state.tooltip = "";
			return "";
		}, "/t");
		register(state -> state.gui.mc.player.getDisplayNameString(), "playername");
		register(state -> state.codes( "\u00A7k"), "k", "obf");
		register(state -> state.codes("\u00A7l"), "l", "bold");
		register(state -> state.codes("\u00A7m"), "m", "strike");
		register(state -> state.codes("\u00A7o"), "o", "italic", "italics");
		register(state -> { state.reset(); return ""; }, "", "reset", "clear");
		register(state -> state.color(state.font.getColorCode('0')), "nocolor");

		FUNCTIONS.put("k", (parameter, state) -> {
			KeyBinding result = getKeybindKey(state, parameter);
			if (result == null) {
				state.tooltip = I18n.format("patchouli.gui.lexicon.keybind_missing", parameter);
				return "N/A";
			}

			state.tooltip = I18n.format("patchouli.gui.lexicon.keybind", I18n.format(result.getKeyDescription()));
			return result.getDisplayName();
		});
		FUNCTIONS.put("l", (parameter, state) -> {
			state.cluster = new LinkedList<>();

			state.prevColor = state.color;
			state.color = state.book.linkColor;
			boolean isExternal = parameter.matches("^https?\\:.*");

			if (isExternal) {
				state.tooltip = I18n.format("patchouli.gui.lexicon.external_link");
				state.isExternalLink = true;
				state.onClick = () -> GuiBook.openWebLink(parameter);
			} else {
				ResourceLocation href = new ResourceLocation(state.book.getModNamespace(), parameter);
				BookEntry entry = state.book.contents.entries.get(href);
				if(entry != null) {
					state.tooltip = entry.isLocked() ? (TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.locked")) : entry.getName();
					GuiBook gui = state.gui;
					Book book = state.book;
					state.onClick = () -> {
						gui.displayLexiconGui(new GuiBookEntry(book, entry), true);
						GuiBook.playBookFlipSound(book);
					};
				} else {
					state.tooltip = "BAD LINK: " + parameter;
				}
			}
			return "";
		});
		FUNCTIONS.put("tooltip", (parameter, state) -> {
			state.tooltip = parameter;
			state.cluster = new LinkedList<>();
			return "";
		});
	}

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

		this.font = gui.mc.fontRenderer;
		this.spaceWidth = font.getStringWidth(" ");
	}

	public List<Word> parse(String text) {
		boolean wasUnicode = font.getUnicodeFlag();
		font.setUnicodeFlag(true);

		List<Word> words = new ArrayList<>();

		String actualText = text;
		if(actualText == null)
			actualText = "[ERROR]";

		for(String key : book.macros.keySet())
			actualText = actualText.replace(key, book.macros.get(key));

		SpanState state = new SpanState(gui, book, x, lineHeight, baseColor, font);
		state.x = x;
		state.y = y;
		state.length = 0;
		state.color = baseColor;
		state.prevColor = baseColor;

		int from = 0;
		char[] chars = actualText.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ' ') {
				processToken(words, state, actualText.substring(from, i), true);
				from = i + 1;
			} else if (chars[i] == '$' && i + 1 < chars.length && chars[i + 1] == '(') {
				if (i > from)
					processToken(words, state, actualText.substring(from, i), false);

				from = i;
				while (i < chars.length && chars[i] != ')')
					i++;

				if (chars[i] != ')') {
					processToken(words, state, "[ERROR: UNFINISHED COMMAND]", false);
					break;
				}

				try {
					String word = processCommand(state, actualText.substring(from + 2, i));
					if (!word.isEmpty())
						processToken(words, state, word, false);
				} catch (Exception ex) {
					processToken(words, state, "[ERROR]", false);
				}

				from = i + 1;
			}
		}
		if (from < chars.length)
			processToken(words, state, actualText.substring(from), false);

		font.setUnicodeFlag(wasUnicode);
		return words;
	}

	private void processToken(List<Word> words, SpanState state, String text, boolean space) {
		if (text.isEmpty() && !space)
			return;

		int trimWidth = font.getStringWidth(state.codes + text);
		int strWidth = trimWidth + (space ? spaceWidth : 0);

		int newLen = state.length + strWidth;

		if(newLen > width) {
			int newTrimLen = state.length + trimWidth;
			if(newTrimLen > width) {
				state.length = strWidth;
				state.x = x;
				state.y += lineHeight;
			} else state.length = newTrimLen;
		} else state.length = newLen;

		Word word = new Word(gui, font, state, text, strWidth);
		words.add(word);
		if(state.cluster != null)
			state.cluster.add(word);
		else
			state.tooltip = "";

		state.x += strWidth;
	}

	private String processCommand(SpanState state, String cmd) {
		state.endingExternal = false;
		String result = "";

		if (cmd.length() == 1 && cmd.matches("^[0123456789abcdef]$")) { // Vanilla colors
			state.color = font.getColorCode(cmd.charAt(0));
			return "";
		}
		else if(cmd.startsWith("#") && (cmd.length() == 4 || cmd.length() == 7)) { // Hex colors
			String parse = cmd.substring(1);
			if(parse.length() == 3)
				parse = "" + parse.charAt(0) + parse.charAt(0) + parse.charAt(1) + parse.charAt(1) + parse.charAt(2) + parse.charAt(2);
			try {
				state.color = Integer.parseInt(parse, 16);
			} catch(NumberFormatException e) {
				state.color = baseColor;
			}
			return "";
		}
		else if (cmd.matches("li\\d?")) { // List Element
			char c = cmd.length() > 2 ? cmd.charAt(2) : '1';
			int dist = Character.isDigit(c) ? Character.digit(c, 10) : 1;
			int pad = dist * 4;
			char bullet = dist % 2 == 0 ? '\u25E6' : '\u2022';
			if(state.y > y || state.x > x)
				state.y += lineHeight;
			state.length = pad;
			state.x = x + pad;

			return TextFormatting.BLACK + "" + bullet + " ";
		}

		if (cmd.indexOf(':') > 0) {
			int index = cmd.indexOf(':');
			String function = cmd.substring(0, index);
			String parameter = cmd.substring(index + 1);
			if (FUNCTIONS.containsKey(function)) {
				result = FUNCTIONS.get(function).process(parameter, state);
			} else {
				result = "[MISSING FUNCTION: " + function + "]";
			}
		}
		else if (COMMANDS.containsKey(cmd)) {
			result = COMMANDS.get(cmd).process(state);
		}

		if(state.endingExternal)
			result += TextFormatting.GRAY + "\u21AA";

		return result;
	}

	private static KeyBinding getKeybindKey(SpanState state, String keybind) {
		String alt = "key." + keybind;

		KeyBinding[] keys = state.gui.mc.gameSettings.keyBindings;
		for(KeyBinding k : keys) {
			String name = k.getKeyDescription();
			if(name.equals(keybind) || name.equals(alt))
				return k;
		}

		return null;
	}
	
	public interface CommandProcessor {
		String process(SpanState state);
	}
	
	public interface FunctionProcessor {
		String process(String parameter, SpanState state);
	}

}
