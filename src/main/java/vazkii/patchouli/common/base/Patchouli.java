package vazkii.patchouli.common.base;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;

public class Patchouli implements ModInitializer {

	public static boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment();

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public Patchouli() {
		PatchouliSounds.init();
		PatchouliItems.init();
	}

	@Override
	public void onInitialize() {
		PatchouliConfig.setup();
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);
		NetworkHandler.registerMessages();

		BookRegistry.INSTANCE.init();

		ReloadContentsHandler.init();
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		OpenBookCommand.register(dispatcher);
	}
}
