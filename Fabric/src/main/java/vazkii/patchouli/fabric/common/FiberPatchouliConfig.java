package vazkii.patchouli.fabric.common;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.EnumConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FiberPatchouliConfig {
    public static PropertyMirror<Boolean> disableAdvancementLocking = PropertyMirror.create(ConfigTypes.BOOLEAN);
    public static PropertyMirror<List<String>> noAdvancementBooks = PropertyMirror.create(ConfigTypes.makeList(ConfigTypes.STRING));
    public static PropertyMirror<Boolean> testingMode = PropertyMirror.create(ConfigTypes.BOOLEAN);
    public static PropertyMirror<String> inventoryButtonBook = PropertyMirror.create(ConfigTypes.STRING);
    public static PropertyMirror<Boolean> useShiftForQuickLookup = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static final EnumConfigType<PatchouliConfig.TextOverflowMode> OVERFLOW_TYPE = ConfigTypes.makeEnum(PatchouliConfig.TextOverflowMode.class);
    public static PropertyMirror<PatchouliConfig.TextOverflowMode> overflowMode = PropertyMirror.create(OVERFLOW_TYPE);

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

            .beginValue("textOverflowMode", OVERFLOW_TYPE, PatchouliConfig.TextOverflowMode.RESIZE)
            .withComment("Set how to handle text overflow: OVERFLOW the text off the page, TRUNCATE overflowed text, or RESIZE everything to fit. Relogin after changing.")
            .finishValue(overflowMode::mirror)

            .build();

    private static void writeDefaultConfig(Path path, JanksonValueSerializer serializer) {
        try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(CONFIG, s, serializer);
        } catch (IOException ignored) {}

    }

    public static void setup() {
        JanksonValueSerializer serializer = new JanksonValueSerializer(false);
        Path p = Paths.get("config", PatchouliAPI.MOD_ID + ".json5");
        writeDefaultConfig(p, serializer);

        try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(CONFIG, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            PatchouliAPI.LOGGER.error("Error loading config", e);
        }
    }
}
