package vazkii.patchouli.common.base;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.command.ServerCommandSource;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;

public class CommonProxy implements ModInitializer {

	@Override
	public void onInitialize() {
		PatchouliConfig.setup();
		PatchouliAPI.get(); // Force this so that legacy instance field gets assigned at about the same time it used to be. TODO 1.17 remove this.
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

		PatchouliSounds.preInit();
		BookRegistry.INSTANCE.init();

		PatchouliItems.init();
		ReloadContentsHandler.init();
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		OpenBookCommand.register(dispatcher);
	}

}
