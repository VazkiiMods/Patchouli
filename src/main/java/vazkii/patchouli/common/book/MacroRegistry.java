package vazkii.patchouli.common.book;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import vazkii.patchouli.api.ICommandProcessor;
import vazkii.patchouli.api.IFunctionProcessor;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.text.SpanState;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;

public class MacroRegistry {
	public static final StringTextComponent EMPTY_STRING_COMPONENT = new StringTextComponent("");

	public static final MacroRegistry INSTANCE = new MacroRegistry();

	public final Map<String, ICommandProcessor> COMMANDS = new HashMap<>();
	public final Map<String, IFunctionProcessor> FUNCTIONS = new HashMap<>();

	public void register(ICommandProcessor handler, String... names) {
		List<String> erroredNames = new ArrayList<>();
		for (String name : names) {
			if (COMMANDS.putIfAbsent(name, handler) != null) {
				erroredNames.add(name);
			}
		}
		if (erroredNames.size() == names.length) {
			throw new IllegalArgumentException("Command Processor not registered for the given names (" + String.join(",", erroredNames) + ")");
		}
	}

	public void register(IFunctionProcessor handler, String... names) {
		List<String> erroredNames = new ArrayList<>();
		for (String name : names) {
			if (FUNCTIONS.putIfAbsent(name, handler) != null) {
				erroredNames.add(name);
			}
		}
		if (erroredNames.size() == names.length) {
			throw new IllegalArgumentException("Command Processor not registered for the given names (" + String.join(",", erroredNames) + ")");
		}
	}

	public boolean functionExists(String function) {
		return FUNCTIONS.containsKey(function);
	}

	public String processFunction(String function, String parameter, SpanState state) {
		return FUNCTIONS.get(function).process(parameter, state);
	}

	public boolean commandExists(String cmd) {
		return COMMANDS.containsKey(cmd);
	}

	public String processCommand(String cmd, SpanState state) {
		return COMMANDS.get(cmd).process(state);
	}

	public void init() {
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
			state.color = state.prevColor;
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
		register(state -> state.gui.getMinecraft().player.getDisplayName().getFormattedText(), "playername");
		register(state -> state.codes("\u00A7k"), "k", "obf");
		register(state -> state.codes("\u00A7l"), "l", "bold");
		register(state -> state.codes("\u00A7m"), "m", "strike");
		register(state -> state.codes("\u00A7o"), "o", "italic", "italics");
		register(state -> {
			state.reset();
			return "";
		}, "", "reset", "clear");
		register(state -> state.color(state.baseColor), "nocolor");

		register((parameter, state) -> {
			KeyBinding result = getKeybindKey(state, parameter);
			if (result == null) {
				state.tooltip = new TranslationTextComponent("patchouli.gui.lexicon.keybind_missing", parameter);
				return "N/A";
			}

			state.tooltip = new TranslationTextComponent("patchouli.gui.lexicon.keybind", new TranslationTextComponent(result.getKeyDescription()));
			return result.getLocalizedName();
		}, "k");
		register((parameter, state) -> {
			state.cluster = new LinkedList<>();

			state.prevColor = state.color;
			state.color = state.book.linkColor;
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
							? new TranslationTextComponent("patchouli.gui.lexicon.locked").applyTextStyle(TextFormatting.GRAY)
							: new StringTextComponent(entry.getName());
					GuiBook gui = state.gui;
					Book book = state.book;
					int page = 0;
					if (anchor != null) {
						int anchorPage = entry.getPageFromAnchor(anchor);
						if (anchorPage >= 0) {
							page = anchorPage / 2;
						} else {
							state.tooltip.appendText(" (INVALID ANCHOR:" + anchor + ")");
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
			state.prevColor = state.color;
			state.color = state.book.linkColor;
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
			state.color = state.prevColor;
			state.cluster = null;
			state.tooltip = EMPTY_STRING_COMPONENT;
			state.onClick = null;
			return "";
		}, "/c");
		register((parameter, state) -> ItemStackUtil.loadStackFromString(parameter).getDisplayName().getFormattedText(), "iname");
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
}
