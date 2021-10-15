package vazkii.patchouli.common.base;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.patchouli.client.base.ClientInitializer;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;

public class Patchouli {

	public static final boolean debug = !FMLEnvironment.production;

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public Patchouli() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) -> this.onInitialize());
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent e) -> new ClientInitializer().onInitializeClient()));
	}

	public void onInitialize() {
        PatchouliConfig.setup();
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> {
			registerCommands(e.getDispatcher(), e.getEnvironment() == Commands.CommandSelection.DEDICATED);
		});
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			var result = LecternEventHandler.rightClick(e.getPlayer(), e.getWorld(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});

		PatchouliSounds.preInit();
		BookRegistry.INSTANCE.init();

		PatchouliItems.init();
		ReloadContentsHandler.init();
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		OpenBookCommand.register(dispatcher);
	}
}
