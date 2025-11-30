package vazkii.patchouli.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.server.packs.PackType;

import vazkii.patchouli.client.base.BookCompletionModelProperty;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.BookReloadHook;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.hud.BookOverlayHud;
import vazkii.patchouli.client.hud.MultiblockProgressHud;
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
		HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, BookOverlayHud.ID, BookOverlayHud::render);
		HudElementRegistry.attachElementBefore(VanillaHudElements.BOSS_BAR, MultiblockProgressHud.ID, MultiblockProgressHud::render);
		UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
		UseBlockCallback.EVENT.register(MultiblockVisualizationHandler.INSTANCE::onPlayerInteract);
		ClientTickEvents.END_CLIENT_TICK.register(MultiblockVisualizationHandler.INSTANCE::onClientTick);
		ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.TYPE, FabricMessageOpenBookGui::handle);
		ClientPlayNetworking.registerGlobalReceiver(MessageReloadBookContents.TYPE, FabricMessageReloadBookContents::handle);

		ItemModels.ID_MAPPER.put(BookModel.Unbaked.ID, BookModel.Unbaked.MAP_CODEC);
		RangeSelectItemModelProperties.ID_MAPPER.put(BookCompletionModelProperty.ID, BookCompletionModelProperty.MAP_CODEC);

		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BookContentResourceListenerLoader.ID, BookContentResourceListenerLoader.INSTANCE);
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BookReloadHook.ID, BookReloadHook.INSTANCE);
	}
}
