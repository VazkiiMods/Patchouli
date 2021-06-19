package vazkii.patchouli.common.base;

import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Patchouli {

	public static boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
}
