package vazkii.patchouli.common.item;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.common.book.BookRegistry;

@EventBusSubscriber(bus = Bus.MOD)
public class PatchouliItems {

	@ObjectHolder("patchouli:guide_book")
	public static Item book;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			ModelLoader.addSpecialModel(new ModelResourceLocation(b.model, "inventory"));
		});
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void replaceModel(ModelBakeEvent event) {
		ModelResourceLocation key = new ModelResourceLocation(book.getRegistryName(), "inventory");
		IBakedModel oldModel = event.getModelRegistry().get(key);
		event.getModelRegistry().put(key, new BookModel(oldModel));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemModBook());
	}
}
