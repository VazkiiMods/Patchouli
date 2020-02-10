package vazkii.patchouli.common.base;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class PatchouliConfig {

	public static ForgeConfigSpec.ConfigValue<Boolean> disableAdvancementLocking;
	public static ForgeConfigSpec.ConfigValue<Boolean> testingMode;
	public static ForgeConfigSpec.ConfigValue<String> inventoryButtonBook;

	private static Map<String, Boolean> configFlags = new ConcurrentHashMap<>();

	public static void setup() {
		Pair<Loader, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Loader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
	}

	public static void load() {
		List<ModInfo> mods = ModList.get().getMods();
		for(ModInfo info : mods)
			setFlag("mod:" + info.getModId(), true);

		setFlag("debug", Patchouli.debug);

		updateFlags();
	}
	
	static class Loader {

		public Loader(ForgeConfigSpec.Builder builder) {
			builder.push("general");

			disableAdvancementLocking = builder
					.comment("Set this to true to disable advancement locking and make all entries visible at all times\\nConfig Flag: advancements_disabled")
					.define("Disable Advancement Locking", false);

			testingMode = builder
					.comment("Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want.\\nConfig Flag: testing_mode")
					.define("Testing Mode", false);

			inventoryButtonBook = builder
					.comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
					.define("Inventory Button Book", "");

			builder.pop();
		}

	}

	private static void updateFlags() {
		setFlag("advancements_disabled", disableAdvancementLocking.get());
		setFlag("testing_mode", testingMode.get());
	}

	public static boolean getConfigFlag(String name) {
		if(name.startsWith("&"))
			return getConfigFlagAND(name.replaceAll("[&|]", "").split(","));
		if(name.startsWith("|"))
			return getConfigFlagOR(name.replaceAll("[&|]", "").split(","));

		boolean target = true;
		if(name.startsWith("!")) {
			name = name.substring(1);
			target = false;
		}
		name = name.trim().toLowerCase();

		return (configFlags.containsKey(name) && configFlags.get(name)) == target;
	}

	public static boolean getConfigFlagAND(String[] tokens) {
		for(String s : tokens)
			if(!getConfigFlag(s))
				return false;

		return true;
	}

	public static boolean getConfigFlagOR(String[] tokens) {
		for(String s : tokens)
			if(getConfigFlag(s))
				return true;

		return false;
	}

	public static void setFlag(String flag, boolean value) {
		configFlags.put(flag.trim().toLowerCase(), value);
	}


}
