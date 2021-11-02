package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

import java.util.Map;

public class ClientInitializer {
	// These events fire even earlier than onInitializeClient is called, even though we already call
	// onInitializeClient from the mod constructor. Great design (tm).
	// So we have to use EventBusSubscriber to get registered even earlier,
	// even though we usually like to use addListener to register event handlers.
	@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ClownyModLoaderDesign {
		@SubscribeEvent
		public static void modelRegistry(ModelRegistryEvent e) {
			BookRegistry.INSTANCE.books.values().stream()
					.map(b -> new ModelResourceLocation(b.model, "inventory"))
					.forEach(ModelLoader::addSpecialModel);

			ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
			ItemProperties.register(PatchouliItems.BOOK, new ResourceLocation(Patchouli.MOD_ID, "completion"), prop);
		}

		@SubscribeEvent
		public static void registerReloadListeners(RegisterClientReloadListenersEvent e) {
			e.registerReloadListener(new ResourceManagerReloadListener() {
				@Override
				public void onResourceManagerReload(ResourceManager manager) {
					if (Minecraft.getInstance().level != null) {
						Patchouli.LOGGER.info("Reloading resource pack-based books, world is nonnull");
						ClientBookRegistry.INSTANCE.reload(true);
					} else {
						Patchouli.LOGGER.info("Not reloading resource pack-based books as client world is missing");
					}
				}
			});
		}
	}

	public void onInitializeClient() {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTicker.init();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();

		MinecraftForge.EVENT_BUS.addListener((TickEvent.RenderTickEvent e) -> {
			if (e.phase == TickEvent.Phase.START) {
				ClientTicker.renderTickStart(e.renderTickTime);
			} else {
				ClientTicker.renderTickEnd();
			}
		});

		MinecraftForge.EVENT_BUS.addListener((RenderWorldLastEvent e) -> {
			MultiblockVisualizationHandler.onWorldRenderLast(e.getMatrixStack());
		});

		MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedOutEvent e) -> {
			ClientAdvancements.playerLogout();
		});

		MinecraftForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) -> {
			TooltipHandler.onTooltip(e.getMatrixStack(), e.getStack(), e.getX(), e.getY());
		});

		FMLJavaModLoadingContext.get().getModEventBus().addListener((ModelBakeEvent e) -> {
			replaceBookModel(e.getModelLoader(), e.getModelRegistry());
		});
	}

	public static void replaceBookModel(ModelBakery loader, Map<ResourceLocation, BakedModel> bakedRegistry) {
		ModelResourceLocation key = new ModelResourceLocation(PatchouliItems.BOOK_ID, "inventory");
		BakedModel oldModel = bakedRegistry.get(key);
		if (oldModel != null) {
			bakedRegistry.put(key, new BookModel(oldModel, loader));
		}
	}
}
