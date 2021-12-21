package vazkii.patchouli.common.base;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.XplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

public class PatchouliConfig {
	private static final Map<String, Boolean> CONFIG_FLAGS = new ConcurrentHashMap<>();

	public static ConfigAccess ACCESS = null;
	public record ConfigAccess(
			Supplier<Boolean> disableAdvancementLocking,
			Supplier<List<String>> noAdvancementBooks,
			Supplier<Boolean> testingMode,
			Supplier<ResourceLocation> inventoryButtonBook,
			Supplier<Boolean> useShiftForQuickLookup,
			Supplier<TextOverflowMode> overflowMode
	) {}

	public static void reloadBuiltinFlags() {
		Collection<XplatModContainer> mods = XplatAbstractions.getInstance().getAllMods();
		for (XplatModContainer info : mods) {
			setFlag("mod:" + info.getId(), true);
		}

		setFlag("debug", XplatAbstractions.getInstance().isDevEnvironment());

		setFlag("advancements_disabled", ACCESS.disableAdvancementLocking().get());
		setFlag("testing_mode", ACCESS.testingMode.get());
		for (String book : ACCESS.noAdvancementBooks().get()) {
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
