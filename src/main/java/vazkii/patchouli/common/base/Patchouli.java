package vazkii.patchouli.common.base;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.patchouli.client.base.ClientProxy;

public class Patchouli {

	public static final boolean debug = !FMLEnvironment.production;

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public Patchouli() {
		// TODO mod bus
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> new CommonProxy().onInitialize());
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
			FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent e) -> new ClientProxy().onInitializeClient()));
	}
}
