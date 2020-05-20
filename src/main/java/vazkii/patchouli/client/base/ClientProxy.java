package vazkii.patchouli.client.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookCrashHandler;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.CommonProxy;
import vazkii.patchouli.common.book.BookRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup(event.getSuggestedConfigurationFile().getParentFile().getParentFile());
		
		MinecraftForge.EVENT_BUS.register(ClientTicker.class);
		MinecraftForge.EVENT_BUS.register(ClientAdvancements.class);
		MinecraftForge.EVENT_BUS.register(MultiblockVisualizationHandler.class);
		MinecraftForge.EVENT_BUS.register(BookRightClickHandler.class);
		MinecraftForge.EVENT_BUS.register(InventoryBookButtonHandler.class);
		FMLCommonHandler.instance().registerCrashCallable(new BookCrashHandler());
	}
	
	@Override
	public void loadComplete(FMLLoadCompleteEvent event) {
		super.loadComplete(event);
		
		BookRegistry.INSTANCE.reload();
	}
	
	@Override
	public void requestBookReload() {
		BookRegistry.INSTANCE.reload();
	}

}
