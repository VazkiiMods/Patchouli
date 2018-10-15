package vazkii.patchouli;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = Patchouli.MOD_ID, name = Patchouli.MOD_NAME, dependencies = Patchouli.DEPENDENCIES)
public class Patchouli {

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String MOD_NAME = "Patchouli";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib;";
	public static final String PREFIX_MOD = MOD_ID + ":";
	
}
