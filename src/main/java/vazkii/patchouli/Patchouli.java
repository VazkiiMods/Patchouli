package vazkii.patchouli;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.common.base.CommonProxy;

// TODO wipe all alquimia lang keys
// TODO rewrite book registry to support multiple books, remove any singleton/static references
// TODO port multiblock system
// TODO port right click handler

@Mod(modid = Patchouli.MOD_ID, name = Patchouli.MOD_NAME, dependencies = Patchouli.DEPENDENCIES)
public class Patchouli {

	// Mod Constants
	public static final String MOD_ID = "patchouli"; // TODO: ensure lack of hard references
	public static final String MOD_NAME = "Patchouli";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib;";
	public static final String PREFIX_MOD = MOD_ID + ":";
	
	public static final String COMMON_PROXY = "vazkii.patchouli.common.base.CommonProxy";
	public static final String CLIENT_PROXY = "vazkii.patchouli.client.base.ClientProxy";
	
	@Instance(value = MOD_ID)
	public static Patchouli instance;
	
	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
}

