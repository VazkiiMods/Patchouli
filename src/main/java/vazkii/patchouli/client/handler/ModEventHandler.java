package vazkii.patchouli.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Patchouli.MOD_ID, value = Dist.CLIENT)
public class ModEventHandler {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent evt) {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();
		TooltipHandler.init();
		ClientAdvancements.init();
		ClientTicker.init();

		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(ModEventHandler::onRegisterClientReloadListener);
		modEventBus.addListener(ModEventHandler::onModelBake);
		modEventBus.addListener(ModEventHandler::onModelRegister);

		ClientBookRegistry.INSTANCE.reload(false);
	}

	private static void onRegisterClientReloadListener(RegisterClientReloadListenersEvent evt) {
		evt.registerReloadListener((ResourceManagerReloadListener) (manager) -> {
			if (Minecraft.getInstance().level != null) {
				Patchouli.LOGGER.info("Reloading resource pack-based books, world is nonnull");
				ClientBookRegistry.INSTANCE.reload(true);
			} else {
				Patchouli.LOGGER.info("Not reloading resource pack-based books as client world is missing");
			}
		});
	}

	private static void onModelBake(ModelBakeEvent evt) {
		ModelResourceLocation key = new ModelResourceLocation(PatchouliItems.BOOK_ID, "inventory");
		BakedModel oldModel = evt.getModelRegistry().get(key);
		if (oldModel != null) {
			evt.getModelRegistry().put(key, new BookModel(oldModel, evt.getModelLoader()));
		}
	}

	private static void onModelRegister(ModelRegistryEvent e) {
		BookRegistry.INSTANCE.books.values().stream()
				.map(b -> new ModelResourceLocation(b.model, "inventory"))
				.forEach(ModelLoader::addSpecialModel);

		ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
		ItemProperties.register(PatchouliItems.BOOK, new ResourceLocation(Patchouli.MOD_ID, "completion"), prop);
	}
}
