package vazkii.patchouli.common.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookFolderLoader;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.network.NetworkHandler;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		PatchouliAPI.instance = PatchouliAPIImpl.INSTANCE;
		
		PatchouliConfig.preInit();
		
		PatchouliItems.preInit();
		PatchouliSounds.preInit();
		MultiblockRegistry.preInit();
		
		BookFolderLoader.setup(event.getSuggestedConfigurationFile().getParentFile().getParentFile());
		BookRegistry.INSTANCE.init();

		MinecraftForge.EVENT_BUS.register(AdvancementSyncHandler.class);
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkHandler.registerMessages();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event) {
		// NO-OP
	}
	
	public void requestBookReload() {
		// NO-OP
	}
	
	
}
