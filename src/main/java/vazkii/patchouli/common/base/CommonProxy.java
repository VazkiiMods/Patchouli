package vazkii.patchouli.common.base;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;

public class CommonProxy {

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
