package vazkii.patchouli.common.item;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;

@EventBusSubscriber(bus = Bus.MOD)
public class PatchouliItems {

	private static final ResourceLocation BOOK_ID = new ResourceLocation(Patchouli.MOD_ID, "guide_book");
	public static final Item book = new ItemModBook().setRegistryName(BOOK_ID);

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		// This event is fired whenever and on whatever thread the vanilla model loader begins running on
		// It is on the async pool and concurrent with all other things like mods running Common/ClientSetup
		// Thus, we need to ensure books are done loading by the time model loading begins
		try {
			BookRegistry.INSTANCE.loadingBarrier.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			ModelLoader.addSpecialModel(new ModelResourceLocation(b.model, "inventory"));
		});
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void replaceModel(ModelBakeEvent event) {
		ModelResourceLocation key = new ModelResourceLocation(BOOK_ID, "inventory");
		IBakedModel oldModel = event.getModelRegistry().get(key);
		if (oldModel != null) {
			event.getModelRegistry().put(key, new BookModel(oldModel));
		}
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(book);
	}
}
