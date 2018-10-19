package vazkii.patchouli.client.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.client.book.BookRegistry;
import vazkii.patchouli.common.base.CommonProxy;
import vazkii.patchouli.common.item.ModItems;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		ModItems.setModels();
		
		BookRegistry.INSTANCE.init();
		PersistentData.setup(event.getSuggestedConfigurationFile().getParentFile().getParentFile());
		
		MinecraftForge.EVENT_BUS.register(ClientTicker.class);
		MinecraftForge.EVENT_BUS.register(ClientAdvancements.class);
	}
	
}
