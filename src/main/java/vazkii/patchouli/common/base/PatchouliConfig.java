package vazkii.patchouli.common.base;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PatchouliConfig {
	public static final ForgeConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static final ForgeConfigSpec.ConfigValue<List<String>> noAdvancementBooks;
	public static final ForgeConfigSpec.ConfigValue<Boolean> testingMode;
	public static final ForgeConfigSpec.ConfigValue<String> inventoryButtonBook;
	public static final ForgeConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;
	public static final ForgeConfigSpec.EnumValue<TextOverflowMode> overflowMode;

	private static final Map<String, Boolean> CONFIG_FLAGS = new ConcurrentHashMap<>();
	private static final ForgeConfigSpec SPEC;
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		disableAdvancementLocking = builder
				.comment("Set this to true to disable advancement locking for ALL books, making all entries visible at all times. Config Flag: advancements_disabled")
				.define("disableAdvancementLocking", false);

		noAdvancementBooks = builder
				.comment("Granular list of Book ID's to disable advancement locking for, e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>")
				.define("noAdvancementBooks", Collections.emptyList());

		testingMode = builder
				.comment("Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want. Config Flag: testing_mode")
				.define("testingMode", false);

		inventoryButtonBook = builder
				.comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
				.define("inventoryButtonBook", "");

		useShiftForQuickLookup = builder
				.comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
				.define("useShiftForQuickLookup", false);

		overflowMode = builder
				.comment("Set how text overflow should be coped with: overflow the text off the page, truncate overflowed text, or resize everything to fit. Relogin after changing.")
				.defineEnum("textOverflowMode", TextOverflowMode.OVERFLOW);

		SPEC = builder.build();
	}

	public static void setup() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC);
	}

	public static void reloadBuiltinFlags() {
		List<IModInfo> mods = ModList.get().getMods();
		for (IModInfo info : mods) {
			setFlag("mod:" + info.getModId(), true);
		}

		setFlag("debug", Patchouli.debug);

		setFlag("advancements_disabled", disableAdvancementLocking.get());
		setFlag("testing_mode", testingMode.get());
		for (String book : noAdvancementBooks.get()) {
			setFlag("advancements_disabled_" + book, true);
		}
	}

	public static boolean getConfigFlag(String name) {
		if (name.startsWith("&")) {
			return getConfigFlagAND(name.replaceAll("[&|]", "").split(","));
		}
		if (name.startsWith("|")) {
			return getConfigFlagOR(name.replaceAll("[&|]", "").split(","));
		}

		boolean target = true;
		if (name.startsWith("!")) {
			name = name.substring(1);
			target = false;
		}
		name = name.trim().toLowerCase(Locale.ROOT);

		Boolean b = CONFIG_FLAGS.get(name);
		if (b == null) {
			if (!name.startsWith("mod:")) {
				Patchouli.LOGGER.warn("Queried for unknown config flag: {}", name);
			}
			b = false;
		}
		return b == target;
	}

	public static boolean getConfigFlagAND(String[] tokens) {
		for (String s : tokens) {
			if (!getConfigFlag(s)) {
				return false;
			}
		}

		return true;
	}

	public static boolean getConfigFlagOR(String[] tokens) {
		for (String s : tokens) {
			if (getConfigFlag(s)) {
				return true;
			}
		}

		return false;
	}

	public static void setFlag(String flag, boolean value) {
		CONFIG_FLAGS.put(flag.trim().toLowerCase(Locale.ROOT), value);
	}

	public enum TextOverflowMode {
		OVERFLOW,
		TRUNCATE,
		RESIZE;
	}

}
