package vazkii.patchouli.common.base;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		PatchouliSounds.REGISTER.register(modEventBus);
		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
		MinecraftForge.EVENT_BUS.addListener(LecternEventHandler::onRightClick);
		MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
		MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
		PatchouliItems.init();
	}

	private void onCommonSetup(FMLCommonSetupEvent evt) {
		PatchouliConfig.setup();

		NetworkHandler.registerMessages();
	}

	private void onRegisterCommands(RegisterCommandsEvent e) {
		OpenBookCommand.register(e.getDispatcher());
	}

	private void onAddReloadListener(AddReloadListenerEvent evt) {
		evt.addListener(BookRegistry.INSTANCE);
	}

	private void onDatapackSync(OnDatapackSyncEvent evt) {
		// Also reload contents when someone types /reload
		MessageReloadBookContents.sendToAll();
	}
}
