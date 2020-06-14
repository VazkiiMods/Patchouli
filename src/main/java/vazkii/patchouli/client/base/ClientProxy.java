package vazkii.patchouli.client.base;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookCrashHandler;
import vazkii.patchouli.common.base.CommonProxy;

public class ClientProxy extends CommonProxy {

	@Override
	public void start() {
		super.start();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupClient);
	}

	public void setupClient(FMLClientSetupEvent event) {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		CrashReportExtender.registerCrashCallable(new BookCrashHandler());
        // todo 1.16 register property override
	}

	@Override
	public void requestBookReload() {
		ClientBookRegistry.INSTANCE.reload();
	}

}
