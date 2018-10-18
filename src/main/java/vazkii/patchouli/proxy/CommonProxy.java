package vazkii.patchouli.proxy;

import vazkii.patchouli.item.ModItems;

public class CommonProxy {

	public void preInit() {
		ModItems.preInit();
	}
	
	public void init() {
		// NO-OP
	}
	
}
