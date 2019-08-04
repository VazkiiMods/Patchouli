package vazkii.patchouli.client.base;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.CommonProxy;
import vazkii.patchouli.common.book.BookRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void start() {
		super.start();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupClient);
		bus.addListener(this::loadComplete);
	}
	
	public void setupClient(FMLClientSetupEvent event) {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event) {
		BookRegistry.INSTANCE.reload();
	}
	
	@Override
	public void requestBookReload() {
		BookRegistry.INSTANCE.reload();
	}
	
}
