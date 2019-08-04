package vazkii.patchouli.common.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class PatchouliItems {

	public static Item book;

	public static void preInit() {
		book = new ItemModBook();

		MinecraftForge.EVENT_BUS.register(PatchouliItems.class);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		bindBookModel();
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(book);
	}

	private static void bindBookModel() {
		List<ModelResourceLocation> models = new LinkedList<>();
		BookRegistry.INSTANCE.books.values().forEach(b -> models.add(new ModelResourceLocation(b.model, "inventory")));
		ModelBakery.registerItemVariants(book, models.toArray(new ModelResourceLocation[0]));

		ModelLoader.setCustomMeshDefinition(book, (stack) -> {
			Book book = ItemModBook.getBook(stack);
			if (book != null)
				return book.modelResourceLoc;

			return Book.DEFAULT_MODEL_RES;
		});
	}

}
