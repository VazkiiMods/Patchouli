package vazkii.patchouli.common.base;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.patchouli.client.base.ClientProxy;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.item.PatchouliItems;

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
		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
		MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
		MinecraftForge.EVENT_BUS.addListener(LecternEventHandler::onRightClick);
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

	private void onServerStart(FMLServerStartedEvent evt) {
		MinecraftServer server = evt.getServer();
		// Also reload contents when someone types /reload
		ResourceManagerReloadListener listener = m -> MessageReloadBookContents.sendToAll(server);
		((ReloadableResourceManager) server.getResourceManager()).registerReloadListener(listener);
	}
}
