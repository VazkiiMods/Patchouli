package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;

public class ClientInitializer {
	public void onInitializeClient() {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTicker.init();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();
		NetworkHandler.registerMessages();

		MinecraftForge.EVENT_BUS.addListener((ModelRegistryEvent e) -> {
			BookRegistry.INSTANCE.books.values().stream()
					.map(b -> new ModelResourceLocation(b.model, "inventory"))
					.forEach(ModelLoader::addSpecialModel);

			ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
			ItemProperties.register(PatchouliItems.BOOK, new ResourceLocation(Patchouli.MOD_ID, "completion"), prop);
		});

		var resourceManager = ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager());
		resourceManager.registerReloadListener(new ResourceManagerReloadListener() {
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
	}
}
