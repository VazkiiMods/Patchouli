package vazkii.patchouli.client.base;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.gui.GoVoteHandler;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.common.network.NetworkHandler;

public class ClientProxy implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GoVoteHandler.init();
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		ClientTicker.init();
		BookRightClickHandler.init();
		MultiblockVisualizationHandler.init();
		NetworkHandler.registerMessages();
		Patchouli.reloadBookHandler = ClientBookRegistry.INSTANCE::reload;

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, register) -> BookRegistry.INSTANCE.books.values().stream()
				.map(b -> new ModelIdentifier(b.model, "inventory"))
				.forEach(register));

		FabricModelPredicateProviderRegistry.register(PatchouliItems.book,
				new Identifier(Patchouli.MOD_ID, "completion"),
				(stack, world, entity, seed) -> ItemModBook.getCompletion(stack));
	}
}
