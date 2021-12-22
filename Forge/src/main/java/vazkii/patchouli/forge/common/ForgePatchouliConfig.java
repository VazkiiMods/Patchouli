package vazkii.patchouli.forge.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import vazkii.patchouli.common.base.PatchouliConfig;

import java.util.Collections;
import java.util.List;

public class ForgePatchouliConfig {
	public static final ForgeConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static final ForgeConfigSpec.ConfigValue<List<String>> noAdvancementBooks;
	public static final ForgeConfigSpec.ConfigValue<Boolean> testingMode;
	public static final ForgeConfigSpec.ConfigValue<String> inventoryButtonBook;
	public static final ForgeConfigSpec.ConfigValue<Boolean> useShiftForQuickLookup;
	public static final ForgeConfigSpec.EnumValue<PatchouliConfig.TextOverflowMode> overflowMode;

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
				.defineEnum("textOverflowMode", PatchouliConfig.TextOverflowMode.RESIZE);

		SPEC = builder.build();
	}

	public static void setup() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC);
		PatchouliConfig.set(new PatchouliConfig.ConfigAccess(
				disableAdvancementLocking::get,
				noAdvancementBooks::get,
				testingMode::get,
				inventoryButtonBook::get,
				useShiftForQuickLookup::get,
				overflowMode::get
		));
	}
}
