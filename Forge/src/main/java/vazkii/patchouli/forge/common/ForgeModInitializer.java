package vazkii.patchouli.forge.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.forge.network.ForgeNetworkHandler;

@Mod.EventBusSubscriber(modid = PatchouliAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(PatchouliAPI.MOD_ID)
public class ForgeModInitializer {
	public ForgeModInitializer() {
		ForgePatchouliConfig.setup();
	}

	@SubscribeEvent
	public static void registerSound(RegistryEvent.Register<SoundEvent> evt) {
		PatchouliSounds.submitRegistrations((id, e) -> evt.getRegistry().register(e.setRegistryName(id)));
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> evt) {
		PatchouliItems.submitItemRegistrations((id, e) -> evt.getRegistry().register(e.setRegistryName(id)));
	}

	@SubscribeEvent
	public static void registerRecipeSerializer(RegistryEvent.Register<RecipeSerializer<?>> evt) {
		PatchouliItems.submitRecipeSerializerRegistrations((id, e) -> evt.getRegistry().register(e.setRegistryName(id)));
	}

	@SubscribeEvent
	public static void onInitialize(FMLCommonSetupEvent evt) {
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> OpenBookCommand.register(e.getDispatcher()));
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			var result = LecternEventHandler.rightClick(e.getPlayer(), e.getWorld(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});

		ForgeNetworkHandler.registerMessages();

		BookRegistry.INSTANCE.init();

		MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent e) -> ReloadContentsHandler.dataReloaded(e.getServer(), e.getServer().getServerResources(), true));
	}
}
