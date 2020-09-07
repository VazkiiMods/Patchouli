package vazkii.patchouli.common.base;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

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
	public static PropertyMirror<Boolean> disableAdvancementLocking = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static PropertyMirror<List<String>> noAdvancementBooks = PropertyMirror.create(ConfigTypes.makeList(ConfigTypes.STRING));
	public static PropertyMirror<Boolean> testingMode = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static PropertyMirror<String> inventoryButtonBook = PropertyMirror.create(ConfigTypes.STRING);
	public static PropertyMirror<Boolean> useShiftForQuickLookup = PropertyMirror.create(ConfigTypes.BOOLEAN);

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
		JanksonValueSerializer serializer = new JanksonValueSerializer(false);
		Path p = Paths.get("config", Patchouli.MOD_ID + ".json5");
		writeDefaultConfig(p, serializer);

		try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
			FiberSerialization.deserialize(CONFIG, s, serializer);
		} catch (IOException | ValueDeserializationException e) {
			Patchouli.LOGGER.error("Error loading config", e);
		}
	}

	public static void reloadFlags() {
		CONFIG_FLAGS.clear();
		Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
		for (ModContainer info : mods) {
			setFlag("mod:" + info.getMetadata().getId(), true);
		}

		setFlag("debug", Patchouli.debug);

		setFlag("advancements_disabled", disableAdvancementLocking.getValue());
		setFlag("testing_mode", testingMode.getValue());
		for (String book : noAdvancementBooks.getValue()) {
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

}
