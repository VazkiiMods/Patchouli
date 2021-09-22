package vazkii.patchouli.common.base;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.patchouli.client.base.ClientInitializer;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

@Mod(Patchouli.MOD_ID)
public class Patchouli {

	public static final boolean debug = !FMLEnvironment.production;

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public Patchouli() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::onCommonSetup);
		modEventBus.addListener(ClientInitializer::onInitializeClient);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientInitializer::preInitClient);
		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
		MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
		MinecraftForge.EVENT_BUS.addListener(LecternEventHandler::onRightClick);
		PatchouliSounds.init();
		PatchouliItems.init();
	}

	private void onCommonSetup(FMLCommonSetupEvent evt) {
		PatchouliConfig.setup();

		NetworkHandler.registerMessages();
		BookRegistry.INSTANCE.init();
	}

	private void onRegisterCommands(RegisterCommandsEvent e) {
		OpenBookCommand.register(e.getDispatcher());
	}

	private void onDatapackSync(OnDatapackSyncEvent evt) {
		var player = evt.getPlayer();
		if (player != null) {
			MessageReloadBookContents.send(player);
		} else {
			MessageReloadBookContents.sendToAll();
		}
	}
}
