package vazkii.patchouli.common.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class PatchouliConfig {

	public static ForgeConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> noAdvancementBooks;
	public static ForgeConfigSpec.ConfigValue<Boolean> testingMode;
	public static ForgeConfigSpec.ConfigValue<String> inventoryButtonBook;
	public static ForgeConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;

	private static final Map<String, Boolean> CONFIG_FLAGS = new HashMap<>();

	public static void setup() {
		Pair<ClientLoader, ForgeConfigSpec> clientSpec = new ForgeConfigSpec.Builder().configure(ClientLoader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec.getRight());
	}

	public static void reloadFlags() {
		CONFIG_FLAGS.clear();
		List<ModInfo> mods = ModList.get().getMods();
		for (ModInfo info : mods) {
			setFlag("mod:" + info.getModId(), true);
		}

		setFlag("debug", Patchouli.debug);

		setFlag("advancements_disabled", disableAdvancementLocking.get());
		setFlag("testing_mode", testingMode.get());
		for (String book : noAdvancementBooks.get()) {
			setFlag("advancements_disabled_" + book, true);
		}
	}

	static class ClientLoader {
		public ClientLoader(ForgeConfigSpec.Builder builder) {
			builder.push("client");
			disableAdvancementLocking = builder
					.comment("Set this to true to disable advancement locking for ALL books, making all entries visible at all times. Config Flag: advancements_disabled")
					.define("Disable Advancement Locking", false);

			noAdvancementBooks = builder.comment("Granular list of Book ID's to disable advancement locking for, e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>")
					.defineList("no_advancement_books", Collections.emptyList(), s -> ResourceLocation.tryCreate((String) s) != null);

			testingMode = builder
					.comment("Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want. Config Flag: testing_mode")
					.define("Testing Mode", false);

			inventoryButtonBook = builder
					.comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
					.define("Inventory Button Book", "");

			useShiftForQuickLookup = builder
					.comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
					.define("quickLookupShift", false);

			builder.pop();
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

}
