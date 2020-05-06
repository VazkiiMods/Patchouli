package vazkii.patchouli.common.book;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import vazkii.patchouli.api.ICommandProcessor;
import vazkii.patchouli.api.IFunctionProcessor;
import vazkii.patchouli.api.ISpanState;
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

	public String processFunction(String function, String parameter, ISpanState state) {
		return FUNCTIONS.get(function).process(parameter, state);
	}

	public boolean commandExists(String cmd) {
		return COMMANDS.containsKey(cmd);
	}

	public String processCommand(String cmd, ISpanState state) {
		return COMMANDS.get(cmd).process(state);
	}

	public void init() {
		register(state -> state.setLineBreaks(1), "br");
		register(state -> state.setLineBreaks(2), "br2", "2br", "p");
		register(state -> {
			state.setEndingExternal(state.isExternalLink());
			state.setColor(state.getPrevColor());
			state.setCluster(null);
			state.setTooltip(EMPTY_STRING_COMPONENT);
			state.setOnClick(null);
			state.setExternalLink(false);
			return "";
		}, "/l");
		register(state -> {
			state.setCluster(null);
			state.setTooltip(EMPTY_STRING_COMPONENT);
			return "";
		}, "/t");
		register(state -> state.getMinecraft().player.getDisplayName().getFormattedText(), "playername");
		register(state -> state.setCodes("\u00A7k"), "k", "obf");
		register(state -> state.setCodes("\u00A7l"), "l", "bold");
		register(state -> state.setCodes("\u00A7m"), "m", "strike");
		register(state -> state.setCodes("\u00A7o"), "o", "italic", "italics");
		register(state -> {
			state.reset();
			return "";
		}, "", "reset", "clear");
		register(state -> state.setColor(state.getBaseColor()), "nocolor");

		register((parameter, state) -> {
			KeyBinding result = getKeybindKey(state, parameter);
			if (result == null) {
				state.setTooltip(new TranslationTextComponent("patchouli.gui.lexicon.keybind_missing", parameter));
				return "N/A";
			}

			state.setTooltip(new TranslationTextComponent("patchouli.gui.lexicon.keybind", new TranslationTextComponent(result.getKeyDescription())));
			return result.getLocalizedName();
		}, "k");
		register((parameter, state) -> {
			state.setCluster(new LinkedList<>());

			state.setPrevColor(state.getColor());
			state.setColor(state.getLinkColor());
			boolean isExternal = parameter.matches("^https?\\:.*");

			if (isExternal) {
				String url = parameter;
				state.setTooltip(new TranslationTextComponent("patchouli.gui.lexicon.external_link"));
				state.setExternalLink(true);
				state.setOnClick(() -> {
					GuiBook.openWebLink(url);
					return true;
				});
			} else {
				int hash = parameter.indexOf('#');
				String anchor = null;
				if (hash >= 0) {
					anchor = parameter.substring(hash + 1);
					parameter = parameter.substring(0, hash);
				}

				Book book = BookRegistry.INSTANCE.books.get(state.getBook());
				ResourceLocation href = parameter.contains(":") ? new ResourceLocation(parameter) : new ResourceLocation(book.getModNamespace(), parameter);
				BookEntry entry = book.contents.entries.get(href);
				if (entry != null) {
					state.setTooltip(entry.isLocked()
							? new TranslationTextComponent("patchouli.gui.lexicon.locked").applyTextStyle(TextFormatting.GRAY)
							: new StringTextComponent(entry.getName()));
//					GuiBook gui = state.getGui();
					int page = 0;
					if (anchor != null) {
						int anchorPage = entry.getPageFromAnchor(anchor);
						if (anchorPage >= 0) {
							page = anchorPage / 2;
						} else {
							state.getTooltip().appendText(" (INVALID ANCHOR:" + anchor + ")");
						}
					}
					int finalPage = page;
					state.setOnClick(() -> {
						PatchouliAPI.instance.openBookEntry(book.id, entry.getId(), finalPage);
						/*GuiBookEntry entryGui = new GuiBookEntry(book, entry, finalPage);
						gui.displayLexiconGui(entryGui, true);*/
//						GuiBook.playBookFlipSound(book);
						return true;
					});
				} else {
					state.setTooltip(new StringTextComponent("BAD LINK: " + parameter));
				}
			}
			return "";
		}, "l");
		register((parameter, state) -> {
			state.setTooltip(new StringTextComponent(parameter));
			state.setCluster(new LinkedList<>());
			return "";
		}, "tooltip", "t");
		register((parameter, state) -> {
			state.setPrevColor(state.getColor());
			state.setColor(state.getLinkColor());
			state.setCluster(new LinkedList<>());
			if (!parameter.startsWith("/")) {
				state.setTooltip(new StringTextComponent("INVALID COMMAND (must begin with /)"));
			} else {
				state.setTooltip(new StringTextComponent(parameter.length() < 20 ? parameter : parameter.substring(0, 20) + "..."));
			}
			state.setOnClick(() -> {
				state.getMinecraft().player.sendChatMessage(parameter);
				return true;
			});
			return "";
		}, "command", "c");
		register(state -> {
			state.setColor(state.getPrevColor());
			state.setCluster(null);
			state.setTooltip(EMPTY_STRING_COMPONENT);
			state.setOnClick(null);
			return "";
		}, "/c");
		register((parameter, state) -> ItemStackUtil.loadStackFromString(parameter).getDisplayName().getFormattedText(), "iname");
	}

	private static KeyBinding getKeybindKey(ISpanState state, String keybind) {
		String alt = "key." + keybind;

		KeyBinding[] keys = state.getMinecraft().gameSettings.keyBindings;
		for (KeyBinding k : keys) {
			String name = k.getKeyDescription();
			if (name.equals(keybind) || name.equals(alt)) {
				return k;
			}
		}

		return null;
	}
}
