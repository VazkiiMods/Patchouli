package vazkii.patchouli.fabric.common;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.CommandSourceStack;

import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.fabric.xplat.FabricXplatImpl;
import vazkii.patchouli.xplat.XplatAbstractions;

public class FabricModInitializer implements ModInitializer {
	public FabricModInitializer() {
		XplatAbstractions.setInstance(new FabricXplatImpl());
		PatchouliSounds.init();
		PatchouliItems.init();
	}

	@Override
	public void onInitialize() {
		FiberPatchouliConfig.setup();
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

		BookRegistry.INSTANCE.init();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ReloadContentsHandler::dataReloaded);
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		OpenBookCommand.register(dispatcher);
	}
}
