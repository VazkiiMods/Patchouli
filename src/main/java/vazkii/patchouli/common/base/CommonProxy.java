package vazkii.patchouli.common.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		PatchouliConfig.preInit();
		
		PatchouliItems.preInit();
		PatchouliSounds.preInit();
		
		BookRegistry.INSTANCE.init();

		MinecraftForge.EVENT_BUS.register(AdvancementSyncHandler.class);
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkHandler.registerMessages();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		// NO-OP
	}
	
	
}
