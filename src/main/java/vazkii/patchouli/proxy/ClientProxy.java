package vazkii.patchouli.proxy;

import vazkii.patchouli.item.ModItems;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		
		ModItems.setModels();
	}
	
}
