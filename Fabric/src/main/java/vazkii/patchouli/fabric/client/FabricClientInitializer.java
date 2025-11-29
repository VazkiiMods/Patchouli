package vazkii.patchouli.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.BookReloadHook;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.fabric.network.FabricMessageOpenBookGui;
import vazkii.patchouli.fabric.network.FabricMessageReloadBookContents;
import vazkii.patchouli.network.MessageOpenBookGui;
import vazkii.patchouli.network.MessageReloadBookContents;

public class FabricClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTickEvents.END_CLIENT_TICK.register(ClientTicker::endClientTick);
		HudRenderCallback.EVENT.register(BookRightClickHandler::onRenderHUD);
		UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
		UseBlockCallback.EVENT.register(MultiblockVisualizationHandler::onPlayerInteract);
		ClientTickEvents.END_CLIENT_TICK.register(MultiblockVisualizationHandler::onClientTick);
		HudRenderCallback.EVENT.register(MultiblockVisualizationHandler::onRenderHUD);
		ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.TYPE, FabricMessageOpenBookGui::handle);
		ClientPlayNetworking.registerGlobalReceiver(MessageReloadBookContents.TYPE, FabricMessageReloadBookContents::handle);

		ModelLoadingPlugin.register(pluginContext -> {
			for (Book book : BookRegistry.INSTANCE.books.values()) {
				PatchouliAPI.LOGGER.info("Adding model {}", book.model);
				pluginContext.addModels(book.model);
			}

			pluginContext.modifyItemModelAfterBake().register(
					(oldModel, ctx) -> {
						if (PatchouliItems.BOOK_ID.equals(ctx.itemId()) && oldModel != null) {
							return new BookModel(oldModel);
						}
						return oldModel;
					}
			);
		});

		ItemProperties.register(PatchouliItems.BOOK,
				ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "completion"),
				(stack, world, entity, seed) -> ItemModBook.getCompletion(stack));

		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BookContentResourceListenerLoader.ID, BookContentResourceListenerLoader.INSTANCE);
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BookReloadHook.ID, BookReloadHook.INSTANCE);
	}
}
