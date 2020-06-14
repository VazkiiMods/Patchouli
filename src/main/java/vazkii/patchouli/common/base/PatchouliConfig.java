package vazkii.patchouli.common.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public class PatchouliConfig {

	public static ForgeConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> noAdvancementBooks;
	public static ForgeConfigSpec.ConfigValue<Boolean> testingMode;
	public static ForgeConfigSpec.ConfigValue<String> inventoryButtonBook;
	public static ForgeConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;

	private static final Map<String, Boolean> CONFIG_FLAGS = new HashMap<>();

	private static final ConfigTree CONFIG = ConfigTree.builder()
			.beginValue("disableAdvancementLocking", ConfigTypes.BOOLEAN, false)
			.withComment("Set this to true to disable advancement locking for ALL books, making all entries visible at all times. Config Flag: advancements_disabled")
			.finishValue(disableAdvancementLocking::mirror)

			.beginValue("noAdvancementBooks", ConfigTypes.makeList(ConfigTypes.STRING), Collections.emptyList())
			.withComment("Granular list of Book ID's to disable advancement locking for, e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>")
			.finishValue(noAdvancementBooks::mirror)

			.beginValue("testingMode", ConfigTypes.BOOLEAN, false)
			.withComment("Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want. Config Flag: testing_mode")
			.finishValue(testingMode::mirror)

			.beginValue("inventoryButtonBook", ConfigTypes.STRING, "")
			.withComment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
			.finishValue(inventoryButtonBook::mirror)

			.beginValue("useShiftForQuickLookup", ConfigTypes.BOOLEAN, false)
			.withComment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
			.finishValue(useShiftForQuickLookup::mirror)

			.build();

	private static void writeDefaultConfig(Path path, JanksonValueSerializer serializer) {
		try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
			FiberSerialization.serialize(CONFIG, s, serializer);
		} catch (IOException ignored) {}

	}

	public static void setup() {
		Pair<Loader, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Loader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());

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

		setFlag("advancements_disabled", disableAdvancementLocking.getValue());
		setFlag("testing_mode", testingMode.getValue());
		for (String book : noAdvancementBooks.getValue()) {
			setFlag("advancements_disabled_" + book, true);
		}
	}

	// todo 1.16: move everything here to client
	static class Loader {

		public Loader(ForgeConfigSpec.Builder builder) {
			builder.push("general");

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

			builder.pop();
		}

	}

	static class ClientLoader {
		public ClientLoader(ForgeConfigSpec.Builder builder) {
			builder.push("client");

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
			Patchouli.LOGGER.warn("Queried for unknown config flag: {}", name);
			return false;
		} else {
			return b == target;
		}
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
