package vazkii.patchouli.common.base;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.common.item.ModItems;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModItems.preInit();
	}
	
	public void init(FMLInitializationEvent event) {
		// NO-OP
	}
	
}
