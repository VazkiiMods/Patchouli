package vazkii.patchouli.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStackTemplate;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.advancement.PatchouliCriteriaTriggers;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliDataComponents;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.network.MessageOpenBookGui;
import vazkii.patchouli.network.MessageReloadBookContents;

@SuppressWarnings("unused")
public class FabricModInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		PatchouliSounds.submitRegistrations((id, e) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, e));
		PatchouliDataComponents.submitDataComponentRegistrations((id, e) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, e));
		PatchouliItems.init();
		PatchouliCriteriaTriggers.submitTriggerRegistrations((id, e) -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, e));
		FiberPatchouliConfig.setup();
		CommandRegistrationCallback.EVENT.register((disp, _, _) -> OpenBookCommand.register(disp));
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

		PayloadTypeRegistry.clientboundPlay().register(MessageOpenBookGui.TYPE, MessageOpenBookGui.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(MessageReloadBookContents.TYPE, MessageReloadBookContents.CODEC);

		BookRegistry.INSTANCE.init();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _, success) -> {
			if (success) {
				ReloadContentsHandler.dataReloaded(server);
			}
		});

		PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			ItemStackTemplate template = api.getBookStackTemplate(b.id);
			if (!b.noBook && template != null) {
				if (b.creativeTab != null) {
					ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, b.creativeTab);
					CreativeModeTabEvents.modifyOutputEvent(key)
							.register(entries -> entries.accept(template.create()));
				}
				CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SEARCH)
						.register(entries -> entries.accept(template.create()));
			}
		});
	}
}
