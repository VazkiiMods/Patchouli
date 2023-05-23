package vazkii.patchouli.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

import org.jetbrains.annotations.Nullable;

public class FabricModInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		PatchouliSounds.submitRegistrations((id, e) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, e));
		PatchouliItems.submitItemRegistrations((id, e) -> Registry.register(BuiltInRegistries.ITEM, id, e));
		PatchouliItems.submitRecipeSerializerRegistrations((id, e) -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, e));
		FiberPatchouliConfig.setup();
		CommandRegistrationCallback.EVENT.register((disp, buildCtx, selection) -> OpenBookCommand.register(disp));
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

		BookRegistry.INSTANCE.init();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _r, success) -> {
			if (success) {
				ReloadContentsHandler.dataReloaded(server);
			}
		});

		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension) {
				if (b.creativeTab != null) {
					ItemGroupEvents.ModifyEntries listener = entries -> entries.accept(ItemModBook.forBook(b));

					CreativeModeTab remappedVanillaTab = mapVanillaCreativeTabForgeIdToFabric(b.creativeTab);
					if (remappedVanillaTab != null) {
						ItemGroupEvents.modifyEntriesEvent(remappedVanillaTab).register(listener);
					} else {
						ItemGroupEvents.modifyEntriesEvent(b.creativeTab).register(listener);
					}
				}
				ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.searchTab()).register(entries -> {
					entries.accept(ItemModBook.forBook(b));
				});
			}
		});
	}

	// Forge and Fabric assign different ID's to the vanilla creative tabs.
	// We want to transparently handle both, so map the ones that differ here.
	// See ForgeModInitializer for this method's dual.
	@Nullable
	private static CreativeModeTab mapVanillaCreativeTabForgeIdToFabric(ResourceLocation oldId) {
		if (!oldId.getNamespace().equals("minecraft")) {
			return null;
		}

		return switch (oldId.getPath()) {
		// The code below looks redundant because forge's names align with mojmap, but
		// Fabric's ID's are different.
		// Forge ID's can be checked in CreativeModeTabRegistry.
		case "natural_blocks" -> CreativeModeTabs.NATURAL_BLOCKS;
		case "functional_blocks" -> CreativeModeTabs.FUNCTIONAL_BLOCKS;
		case "redstone_blocks" -> CreativeModeTabs.REDSTONE_BLOCKS;
		case "tools_and_utilities" -> CreativeModeTabs.TOOLS_AND_UTILITIES;
		case "food_and_drinks" -> CreativeModeTabs.FOOD_AND_DRINKS;
		// NB: Op blocks isn't given an ID on Forge, which seems wrong, but *shrug*
		default -> null;
		};
	}
}
