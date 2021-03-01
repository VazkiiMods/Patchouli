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
import java.util.regex.*;

public class BookTextParser {
	public static final StringTextComponent EMPTY_STRING_COMPONENT = new StringTextComponent("");
	// A command lookup takes the body of a command $(...) and the current span state.
	// If it understands the command, it can modify the span state and return Optional.of(command replacement).
	// Otherwise, it just returns Optional.empty().
	// TODO: Make this part of the API, perhaps?
	private static final List<CommandLookup> COMMAND_LOOKUPS = new ArrayList<>();
	private static final Map<String, CommandProcessor> COMMANDS = new HashMap<>();
	private static final Map<String, FunctionProcessor> FUNCTIONS = new HashMap<>();

	private static void registerProcessor(BiFunction<String, SpanState, Optional<String>> processor) {
		COMMAND_PROCESSORS.add(processor);
	}

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
		registerProcessor(BookTextParser::colorCodeProcessor);
		registerProcessor(BookTextParser::colorHexProcessor);
		registerProcessor(BookTextParser::listProcessor);
		registerProcessor(BookTextParser::lookupFunctionProcessor);
		registerProcessor(BookTextParser::lookupCommandProcessor);
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

			state.pushStyle(Style.EMPTY.setColor(Color.fromInt(state.book.linkColor)));
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
			state.pushStyle(Style.EMPTY.setColor(Color.fromInt(state.book.linkColor)));
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
		this.spaceWidth = font.getStringPropertyWidth(new StringTextComponent(" ").setStyle(baseStyle));
	}

	public List<Word> parse(ITextComponent text) {
		List<Span> spans = new ArrayList<>();
		text.func_230534_b_((string, style) -> {
			spans.addAll(processCommands(expandMacros(string), style));
		}, baseStyle);
		List<Word> words = layout(spans);
		return words;
	}
	public String expandMacros(@Nullable String text) {
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

		return actualText;
	}

	private List<Word> layout(List<Span> spans) {
		TextLayouter layouter = new TextLayouter(gui, x, y, lineHeight, width);
		layouter.layout(font, spans);
		return layouter.getWords();
	}

	private Pattern COMMAND_PATTERN = Pattern.compile("\\$\\(([^)]*)\\)");
	/**
	 * Takes in the raw book source and computes a collection of spans from it.
	 */
	private List<Span> processCommands(String text, Style style) {
		SpanState state = new SpanState(gui, book, style);
		List<Span> spans = new ArrayList<>();
		Matcher match = COMMAND_PATTERN.matcher(text);

		while (matcher.find()) {
			StringBuffer sb = new StringBuffer();
			// Extract the portion before the match to sb
			match.appendReplacement(sb, "");
			spans.add(new Span(state, sb.toString()));

			try {
				String processed = processCommand(state, text.group());
				if (!processed.isEmpty()) {
					spans.add(new Span(state, processed));

					if (state.cluster == null) {
						state.tooltip = EMPTY_STRING_COMPONENT;
					}
				}
			} catch (Exception ex) {
				spans.add(Span.error(state, "[ERROR]"));
			}
		}
		// Extract the portion after all matches
		spans.add(new Span(state, matcher.appendTail(new StringBuffer())));
		return spans;
	}

	private String processCommand(SpanState state, String cmd) {
		state.endingExternal = false;

		Optional<String> optResult;
		for (CommandLookup lookup : COMMAND_LOOKUPS) {
			optResult = lookup.process(cmd, state);
			if (optResult.isPresent()) {
				break;
			}
		}
		String result = optResult.orElse("$(" + cmd + ")");

		if (state.endingExternal) {
			result += TextFormatting.GRAY + "\u21AA";
		}

		return result;
	}

	private static Optional<String> colorCodeProcessor(String functionName, SpanState state) {
		if (functionName.length() == 1 && functionName.matches("^[0123456789abcdef]$")) {
			return Optional.of(state.modifyStyle(s -> s.applyFormatting(TextFormatting.fromFormattingCode(functionName.charAt(0)))));
		}
		return Optional.empty();
	}
	private static Optional<String> colorHexProcessor(String functionName, SpanState state) {
		if (functionName.startsWith("#") && (functionName.length() == 4 || functionName.length() == 7)) {
			Color color;
			String parse = functionName.substring(1);
			if (parse.length() == 3) {
				parse = "" + parse.charAt(0) + parse.charAt(0) + parse.charAt(1) + parse.charAt(1) + parse.charAt(2) + parse.charAt(2);
			}
			try {
				color = Color.fromInt(Integer.parseInt(parse, 16));
			} catch (NumberFormatException e) {
				color = baseStyle.getColor();
			}
			return Optional.of(state.color(color));
		}
		return Optional.empty();
	}

	private static Optional<String> listProcessor(String functionName, SpanState state) {
		if (functionName.matches("li\\d?")) {
			char c = functionName.length() > 2 ? functionName.charAt(2) : '1';
			int dist = Character.isDigit(c) ? Character.digit(c, 10) : 1;
			int pad = dist * 4;
			char bullet = dist % 2 == 0 ? '\u25E6' : '\u2022';
			state.lineBreaks = 1;
			state.spacingLeft = pad;
			state.spacingRight = spaceWidth;
			return Optional.of(TextFormatting.BLACK.toString() + bullet);
		}
		return Optional.empty();
	}

	private static Optional<String> lookupFunctionProcessor(String functionName, SpanState state) {
		int colonIndex = functionName.indexOf(':');
		if (colonIndex > 0) {
			String fname = functionName.substring(0, index), param = functionName.substring(index + 1);
			return Optional.of(
				Optional.ofNullable(FUNCTIONS.get(fname))
						.map(f -> f.process(param, state))
						.orElse("[MISSING FUNCTION: " + fname + "]"));
		}
		return Optional.empty();
	}

	private static Optional<String> lookupCommandProcessor(String functionName, SpanState state) {
		return Optional.ofNullable(COMMANDS.get(functionName)).map(CommandProcessor::process);
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

	public interface CommandLookup {
		Optional<String> process(String commandName, SpanState state);
	}

	public interface CommandProcessor {
		String process(SpanState state);
	}

	public interface FunctionProcessor {
		String process(String parameter, SpanState state);
	}

}
