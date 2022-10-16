package vazkii.patchouli.common.base;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.IXplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PatchouliConfig {
	private static final Map<String, Boolean> CONFIG_FLAGS = new ConcurrentHashMap<>();

	public interface ConfigAccess {
		boolean disableAdvancementLocking();
		List<String> noAdvancementBooks();
		boolean testingMode();
		String inventoryButtonBook();
		boolean useShiftForQuickLookup();
		TextOverflowMode overflowMode();
		int quickLookupTime();
	}

	private static ConfigAccess access = null;

	public static ConfigAccess get() {
		return access;
	}

	public static void set(ConfigAccess a) {
		if (access != null) {
			throw new IllegalStateException("ConfigAccess already set");
		}
		access = a;
	}

	public static void reloadBuiltinFlags() {
		Collection<XplatModContainer> mods = IXplatAbstractions.INSTANCE.getAllMods();
		for (XplatModContainer info : mods) {
			setFlag("mod:" + info.getId(), true);
		}

		setFlag("debug", IXplatAbstractions.INSTANCE.isDevEnvironment());

		setFlag("advancements_disabled", get().disableAdvancementLocking());
		setFlag("testing_mode", get().testingMode());
		for (String book : get().noAdvancementBooks()) {
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
				PatchouliAPI.LOGGER.warn("Queried for unknown config flag: {}", name);
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
		RESIZE
	}

}
