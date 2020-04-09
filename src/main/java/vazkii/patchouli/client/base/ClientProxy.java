package vazkii.patchouli.client.base;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceType;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.shader.ShaderHelper;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;

public class ClientProxy implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTicker.init();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();
		NetworkHandler.registerMessages();
		Patchouli.reloadBookHandler = ClientBookRegistry.INSTANCE::reload;
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ShaderHelper.INSTANCE);

		ModelLoadingRegistry.INSTANCE.registerAppender((manager, register) -> {
			BookRegistry.INSTANCE.books.values().stream()
					.map(b -> new ModelIdentifier(b.model, "inventory"))
					.forEach(register);
		});
	}
}
