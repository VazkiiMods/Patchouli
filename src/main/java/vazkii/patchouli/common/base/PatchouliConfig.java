package vazkii.patchouli.common.base;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class PatchouliConfig {
	// TODO Optional only used to present same interface as forgeconfigvalue. Clean up later.
	public static Optional<Boolean> disableAdvancementLocking = Optional.of(false);
	public static Optional<Boolean> testingMode = Optional.of(false);
	public static Optional<String> inventoryButtonBook = Optional.of("");
	public static Optional<Boolean> useShiftForQuickLookup = Optional.of(false);

	private static Map<String, Boolean> configFlags = new ConcurrentHashMap<>();

	public static void setup() {
	    /* TODO fabric load
		Pair<Loader, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Loader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());

		Pair<ClientLoader, ForgeConfigSpec> clientSpec = new ForgeConfigSpec.Builder().configure(ClientLoader::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec.getRight());
	     */
	}

	public static void load() {
		for(ModContainer info : FabricLoader.getInstance().getAllMods())
			setFlag("mod:" + info.getMetadata().getId(), true);

		setFlag("debug", Patchouli.debug);

		updateFlags();
	}

	/*
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

	static class ClientLoader {
		public ClientLoader(ForgeConfigSpec.Builder builder) {
			builder.push("client");

			useShiftForQuickLookup = builder
					.comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
					.define("quickLookupShift", false);

			builder.pop();
		}
	}
 	*/

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
