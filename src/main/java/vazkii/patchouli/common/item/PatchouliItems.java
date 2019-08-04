package vazkii.patchouli.common.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.patchouli.common.book.BookRegistry;

@EventBusSubscriber(bus = Bus.MOD)
public class PatchouliItems {

	@ObjectHolder("patchouli:guide_book")
	public static Item book;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		bindBookModel();
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemModBook());
	}

	private static void bindBookModel() {
		List<ModelResourceLocation> models = new LinkedList<>();
		BookRegistry.INSTANCE.books.values().forEach(b -> models.add(new ModelResourceLocation(b.model, "inventory")));

		// TODO model mapping
//		ModelBakery.registerItemVariants(book, models.toArray(new ModelResourceLocation[0]));
//
//		ModelLoader.setCustomMeshDefinition(book, (stack) -> {
//			Book book = ItemModBook.getBook(stack);
//			if (book != null)
//				return book.modelResourceLoc;
//
//			return Book.DEFAULT_MODEL_RES;
//		});
	}

}
