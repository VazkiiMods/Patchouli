package vazkii.patchouli.client.book.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.util.*;

public class BookTextParser {
	public static final StringTextComponent EMPTY_STRING_COMPONENT = new StringTextComponent("");
	private static final Map<String, CommandProcessor> COMMANDS = new HashMap<>();
	private static final Map<String, FunctionProcessor> FUNCTIONS = new HashMap<>();

	private static void register(CommandProcessor handler, String... names) {
		for (String name : names) {
			COMMANDS.put(name, handler);
		}
	}

	private static void register(FunctionProcessor function, String... names) {
		for (String name : names) {
			FUNCTIONS.put(name, function);
		}
	}

	static {
		register(state -> {
			state.lineBreaks = 1;
			return "";
		}, "br");
		register(state -> {
			state.lineBreaks = 2;
			return "";
		}, "br2", "2br", "p");
		register(state -> {
			state.endingExternal = state.isExternalLink;
			state.popStyle();
			state.cluster = null;
			state.tooltip = EMPTY_STRING_COMPONENT;
			state.onClick = null;
			state.isExternalLink = false;
			return "";
		}, "/l");
		register(state -> {
			state.cluster = null;
			state.tooltip = EMPTY_STRING_COMPONENT;
			return "";
		}, "/t");
		register(state -> state.gui.getMinecraft().player.getName().getString(), "playername"); // TODO 1.16: dropped format codes
		register(state -> state.modifyStyle(s -> s.applyFormatting(TextFormatting.OBFUSCATED)), "k", "obf");
		register(state -> state.modifyStyle(s -> s.applyFormatting(TextFormatting.BOLD)), "l", "bold");
		register(state -> state.modifyStyle(s -> s.applyFormatting(TextFormatting.STRIKETHROUGH)), "m", "strike");
		register(state -> state.modifyStyle(s -> s.applyFormatting(TextFormatting.ITALIC)), "o", "italic", "italics");
		register(state -> {
			state.reset();
			return "";
		}, "", "reset", "clear");
		register(SpanState::baseColor, "nocolor");

		register((parameter, state) -> {
			KeyBinding result = getKeybindKey(state, parameter);
			if (result == null) {
				state.tooltip = new TranslationTextComponent("patchouli.gui.lexicon.keybind_missing", parameter);
				return "N/A";
			}

			state.tooltip = new TranslationTextComponent("patchouli.gui.lexicon.keybind", new TranslationTextComponent(result.getTranslationKey()));
			return result.func_238171_j_().getString();
		}, "k");
		register((parameter, state) -> {
			state.cluster = new LinkedList<>();

			state.pushStyle(Style.EMPTY.setColor(Color.func_240743_a_(state.book.linkColor)));
			boolean isExternal = parameter.matches("^https?\\:.*");

			if (isExternal) {
				String url = parameter;
				state.tooltip = new TranslationTextComponent("patchouli.gui.lexicon.external_link");
				state.isExternalLink = true;
				state.onClick = () -> {
					GuiBook.openWebLink(url);
					return true;
				};
			} else {
				int hash = parameter.indexOf('#');
				String anchor = null;
				if (hash >= 0) {
					anchor = parameter.substring(hash + 1);
					parameter = parameter.substring(0, hash);
				}

				ResourceLocation href = parameter.contains(":") ? new ResourceLocation(parameter) : new ResourceLocation(state.book.getModNamespace(), parameter);
				BookEntry entry = state.book.contents.entries.get(href);
				if (entry != null) {
					state.tooltip = entry.isLocked()
							? new TranslationTextComponent("patchouli.gui.lexicon.locked").mergeStyle(TextFormatting.GRAY)
							: entry.getName();
					GuiBook gui = state.gui;
					Book book = state.book;
					int page = 0;
					if (anchor != null) {
						int anchorPage = entry.getPageFromAnchor(anchor);
						if (anchorPage >= 0) {
							page = anchorPage / 2;
						} else {
							state.tooltip.appendString(" (INVALID ANCHOR:" + anchor + ")");
						}
					}
					int finalPage = page;
					state.onClick = () -> {
						GuiBookEntry entryGui = new GuiBookEntry(book, entry, finalPage);
						gui.displayLexiconGui(entryGui, true);
						GuiBook.playBookFlipSound(book);
						return true;
					};
				} else {
					state.tooltip = new StringTextComponent("BAD LINK: " + parameter);
				}
			}
			return "";
		}, "l");
		register((parameter, state) -> {
			state.tooltip = new StringTextComponent(parameter);
			state.cluster = new LinkedList<>();
			return "";
		}, "tooltip", "t");
		register((parameter, state) -> {
			state.pushStyle(Style.EMPTY.setColor(Color.func_240743_a_(state.book.linkColor)));
			state.cluster = new LinkedList<>();
			if (!parameter.startsWith("/")) {
				state.tooltip = new StringTextComponent("INVALID COMMAND (must begin with /)");
			} else {
				state.tooltip = new StringTextComponent(parameter.length() < 20 ? parameter : parameter.substring(0, 20) + "...");
			}
			state.onClick = () -> {
				state.gui.getMinecraft().player.sendChatMessage(parameter);
				return true;
			};
			return "";
		}, "command", "c");
		register(state -> {
			state.popStyle();
			state.cluster = null;
			state.tooltip = EMPTY_STRING_COMPONENT;
			state.onClick = null;
			return "";
		}, "/c");
	}

	private final GuiBook gui;
	private final Book book;
	private final int x, y, width;
	private final int lineHeight;
	private final Style baseStyle;
	private final FontRenderer font;
	private final int spaceWidth;

	public BookTextParser(GuiBook gui, Book book, int x, int y, int width, int lineHeight, Style baseStyle) {
		this.gui = gui;
		this.book = book;
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineHeight = lineHeight;
		this.baseStyle = baseStyle;

		this.font = Minecraft.getInstance().fontRenderer;
		this.spaceWidth = font.func_238414_a_(new StringTextComponent(" ").setStyle(baseStyle));
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

	/**
	 * Takes in the raw book source and computes a collection of spans from it.
	 */
	private List<Span> processCommands(String text) {
		SpanState state = new SpanState(gui, book, baseStyle);
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
			return state.modifyStyle(s -> s.applyFormatting(TextFormatting.fromFormattingCode(cmd.charAt(0))));
		} else if (cmd.startsWith("#") && (cmd.length() == 4 || cmd.length() == 7)) { // Hex colors
			Color color;
			String parse = cmd.substring(1);
			if (parse.length() == 3) {
				parse = "" + parse.charAt(0) + parse.charAt(0) + parse.charAt(1) + parse.charAt(1) + parse.charAt(2) + parse.charAt(2);
			}
			try {
				color = Color.func_240743_a_(Integer.parseInt(parse, 16));
			} catch (NumberFormatException e) {
				color = baseStyle.getColor();
			}
			return state.color(color);
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
			if (FUNCTIONS.containsKey(function)) {
				result = FUNCTIONS.get(function).process(parameter, state);
			} else {
				result = "[MISSING FUNCTION: " + function + "]";
			}
		} else if (COMMANDS.containsKey(cmd)) {
			result = COMMANDS.get(cmd).process(state);
		}

		if (state.endingExternal) {
			result += TextFormatting.GRAY + "\u21AA";
		}

		return result;
	}

	private static KeyBinding getKeybindKey(SpanState state, String keybind) {
		String alt = "key." + keybind;

		KeyBinding[] keys = state.gui.getMinecraft().gameSettings.keyBindings;
		for (KeyBinding k : keys) {
			String name = k.getKeyDescription();
			if (name.equals(keybind) || name.equals(alt)) {
				return k;
			}
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
