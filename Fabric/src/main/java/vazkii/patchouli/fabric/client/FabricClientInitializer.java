package vazkii.patchouli.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.fabric.network.FabricMessageOpenBookGui;
import vazkii.patchouli.fabric.network.FabricMessageReloadBookContents;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
		ClientPlayNetworking.registerGlobalReceiver(FabricMessageOpenBookGui.ID, FabricMessageOpenBookGui::handle);
		ClientPlayNetworking.registerGlobalReceiver(FabricMessageReloadBookContents.ID, FabricMessageReloadBookContents::handle);

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, register) -> BookRegistry.INSTANCE.books.values().stream()
				.map(b -> new ModelResourceLocation(b.model, "inventory"))
				.forEach(register));

		ItemProperties.register(PatchouliItems.BOOK,
				new ResourceLocation(PatchouliAPI.MOD_ID, "completion"),
				(stack, world, entity, seed) -> ItemModBook.getCompletion(stack));

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
			private static final ResourceLocation id = new ResourceLocation(PatchouliAPI.MOD_ID, "resource_pack_books");

			@Override
			public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
				return BookContentResourceListenerLoader.INSTANCE.reload(barrier, manager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
			}

			@Override
			public ResourceLocation getFabricId() {
				return id;
			}
		});
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private static final ResourceLocation id = new ResourceLocation(PatchouliAPI.MOD_ID, "reload_hook");

			@Override
			public ResourceLocation getFabricId() {
				return id;
			}

			@Override
			public void onResourceManagerReload(ResourceManager manager) {
				if (Minecraft.getInstance().level != null) {
					PatchouliAPI.LOGGER.info("Reloading resource pack-based books, world is nonnull");
					ClientBookRegistry.INSTANCE.reload(true);
				} else {
					PatchouliAPI.LOGGER.info("Not reloading resource pack-based books as client world is missing");
				}
			}
		});
	}
}
