package vazkii.patchouli.common.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.ModItems;
import vazkii.patchouli.common.network.GuiHandler;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModItems.preInit();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Patchouli.instance, new GuiHandler());
		
		MinecraftForge.EVENT_BUS.register(new AdvancementSyncHandler());
	}
	
	public void init(FMLInitializationEvent event) {
		// NO-OP
	}
	
}
