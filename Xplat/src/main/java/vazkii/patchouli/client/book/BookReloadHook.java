package vazkii.patchouli.client.book;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import vazkii.patchouli.api.PatchouliAPI;

public final class BookReloadHook implements ResourceManagerReloadListener {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "reload_hook");
	public static final ResourceManagerReloadListener INSTANCE = new BookReloadHook();

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		if (Minecraft.getInstance().level != null) {
			PatchouliAPI.LOGGER.info("Reloading resource pack-based books");
			ClientBookRegistry.INSTANCE.reload();
		} else {
			PatchouliAPI.LOGGER.debug("Not reloading resource pack-based books as client world is missing");
		}
	}
}
