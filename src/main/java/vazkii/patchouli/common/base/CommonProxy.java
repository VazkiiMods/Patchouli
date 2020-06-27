package vazkii.patchouli.common.base;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;

public class CommonProxy {

	public void start() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);

		PatchouliConfig.setup();
		PatchouliAPI.instance = PatchouliAPIImpl.INSTANCE;
	}

	public void setup(FMLCommonSetupEvent event) {
		PatchouliSounds.preInit();
		BookRegistry.INSTANCE.init();

		NetworkHandler.registerMessages();
	}

	public void requestBookReload() {
		// NO-OP
	}

}
