package vazkii.patchouli.client.base;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;

import java.util.Map;

public class ClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTicker.init();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();
		NetworkHandler.registerMessages();

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, register) -> BookRegistry.INSTANCE.books.values().stream()
				.map(b -> new ModelResourceLocation(b.model, "inventory"))
				.forEach(register));

		FabricModelPredicateProviderRegistry.register(PatchouliItems.BOOK,
				new ResourceLocation(Patchouli.MOD_ID, "completion"),
				(stack, world, entity, seed) -> ItemModBook.getCompletion(stack));

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private final ResourceLocation id = new ResourceLocation(Patchouli.MOD_ID, "resource_pack_books");

			@Override
			public ResourceLocation getFabricId() {
				return id;
			}

			@Override
			public void onResourceManagerReload(ResourceManager manager) {
				if (Minecraft.getInstance().level != null) {
					Patchouli.LOGGER.info("Reloading resource pack-based books, world is nonnull");
					ClientBookRegistry.INSTANCE.reload(true);
				} else {
					Patchouli.LOGGER.info("Not reloading resource pack-based books as client world is missing");
				}
			}
		});
	}

	public static void replaceBookModel(ModelBakery loader, Map<ResourceLocation, BakedModel> bakedRegistry) {
		ModelResourceLocation key = new ModelResourceLocation(PatchouliItems.BOOK_ID, "inventory");
		BakedModel oldModel = bakedRegistry.get(key);
		if (oldModel != null) {
			bakedRegistry.put(key, new BookModel(oldModel, loader));
		}
	}
}
