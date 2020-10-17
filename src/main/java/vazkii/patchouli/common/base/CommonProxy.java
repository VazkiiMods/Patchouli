package vazkii.patchouli.common.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.network.NetworkHandler;

public class CommonProxy {

	public void start() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);

		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

		PatchouliConfig.setup();
		PatchouliAPI.instance = PatchouliAPIImpl.INSTANCE;
	}

	private void registerCommands(RegisterCommandsEvent evt) {
		OpenBookCommand.register(evt.getDispatcher());
	}

	public void setup(FMLCommonSetupEvent event) {
		PatchouliSounds.preInit();
		NetworkHandler.registerMessages();
	}

	public void requestBookReload() {
		// NO-OP
	}

}
