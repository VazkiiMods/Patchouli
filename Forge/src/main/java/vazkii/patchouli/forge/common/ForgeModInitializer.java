package vazkii.patchouli.forge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.advancement.PatchouliCriteriaTriggers;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.forge.network.ForgeNetworkHandler;

@Mod.EventBusSubscriber(modid = PatchouliAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(PatchouliAPI.MOD_ID)
public class ForgeModInitializer {
	public ForgeModInitializer() {
		ForgePatchouliConfig.setup();
	}

	@SubscribeEvent
	public static void register(RegisterEvent evt) {
		evt.register(Registries.SOUND_EVENT, rh -> {
			PatchouliSounds.submitRegistrations(rh::register);
		});
		evt.register(Registries.ITEM, rh -> {
			PatchouliItems.submitItemRegistrations(rh::register);
		});
		evt.register(Registries.RECIPE_SERIALIZER, rh -> {
			PatchouliItems.submitRecipeSerializerRegistrations(rh::register);
		});
	}

	@SubscribeEvent
	public static void processCreativeTabs(BuildCreativeModeTabContentsEvent evt) {
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook) {
				ItemStack book = ItemModBook.forBook(b);
				if (evt.getTab() == CreativeModeTabs.searchTab()) {
					evt.accept(book);
				} else if (b.creativeTab != null) {
					if (evt.getTab() == CreativeModeTabRegistry.getTab(b.creativeTab)) {
						evt.accept(book);
					}
				}
			}
		});
	}

	@SubscribeEvent
	public static void onInitialize(FMLCommonSetupEvent evt) {
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> OpenBookCommand.register(e.getDispatcher()));
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			var result = LecternEventHandler.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});

		ForgeNetworkHandler.registerMessages();

		PatchouliCriteriaTriggers.init();
		BookRegistry.INSTANCE.init();

		MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent e) -> ReloadContentsHandler.dataReloaded(e.getServer()));
	}
}
