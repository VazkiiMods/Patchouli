package vazkii.patchouli;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.proxy.CommonProxy;

@Mod(modid = Patchouli.MOD_ID, name = Patchouli.MOD_NAME, dependencies = Patchouli.DEPENDENCIES)
public class Patchouli {

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String MOD_NAME = "Patchouli";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib;";
	public static final String PREFIX_MOD = MOD_ID + ":";
	
	public static final String COMMON_PROXY = "vazkii.patchouli.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "vazkii.patchouli.proxy.ClientProxy";
	
	@Instance(value = MOD_ID)
	public static Patchouli instance;
	
	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
	
}

