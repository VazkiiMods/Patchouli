package vazkii.patchouli.common.base;

import net.fabricmc.api.ModInitializer;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.PatchouliItems;

public class CommonProxy implements ModInitializer {

	@Override
	public void onInitialize() {
		PatchouliConfig.setup();
		PatchouliAPI.instance = PatchouliAPIImpl.INSTANCE;

		PatchouliConfig.load();

		PatchouliSounds.preInit();
		BookRegistry.INSTANCE.init();

		PatchouliItems.init();
		ReloadContentsHandler.init();
	}

}
