package vazkii.patchouli.common.item;

import com.google.common.base.Stopwatch;

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

import java.util.concurrent.TimeUnit;

@EventBusSubscriber(bus = Bus.MOD)
public class PatchouliItems {

	private static final ResourceLocation BOOK_ID = new ResourceLocation(Patchouli.MOD_ID, "guide_book");
	public static final Item book = new ItemModBook().setRegistryName(BOOK_ID);

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
		ModelResourceLocation key = new ModelResourceLocation(BOOK_ID, "inventory");
		IBakedModel oldModel = event.getModelRegistry().get(key);
		if (oldModel != null) {
			event.getModelRegistry().put(key, new BookModel(oldModel));
		}
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(book);
		// Load book jsons here.
		// Any later (e.g. CommonSetupEvent) and it overlaps concurrently with registerModels above and properly waiting for the former in the latter
		// is truly a world of pain. Don't try.
		Stopwatch timer = Stopwatch.createStarted();
		BookRegistry.INSTANCE.init();
		Patchouli.LOGGER.info("Loaded book jsons on {} in {} ms", Thread.currentThread(), timer.elapsed(TimeUnit.MILLISECONDS));

	}
}
