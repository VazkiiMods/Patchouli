package vazkii.patchouli.client.base;

import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.CrashReportExtender;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.gui.GoVoteHandler;
import vazkii.patchouli.client.handler.BookCrashHandler;
import vazkii.patchouli.common.base.CommonProxy;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

public class ClientProxy extends CommonProxy {

	@Override
	public void start() {
		super.start();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupClient);
	}

	public void setupClient(FMLClientSetupEvent event) {
		GoVoteHandler.init();
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		CrashReportExtender.registerCrashCallable(new BookCrashHandler());

		IItemPropertyGetter prop = (stack, world, entity) -> ItemModBook.getCompletion(stack);
		ItemModelsProperties.func_239418_a_(PatchouliItems.book, new ResourceLocation(Patchouli.MOD_ID, "completion"), prop);
	}

	@Override
	public void requestBookReload() {
		ClientBookRegistry.INSTANCE.reload();
	}

}
