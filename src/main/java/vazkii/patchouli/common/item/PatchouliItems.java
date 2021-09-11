package vazkii.patchouli.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

public class PatchouliItems {

	public static final ResourceLocation BOOK_ID = new ResourceLocation(Patchouli.MOD_ID, "guide_book");
	public static Item BOOK;

	private static void registerItems(RegistryEvent.Register<Item> evt) {
		BOOK = new ItemModBook();
		evt.getRegistry().register(BOOK.setRegistryName(BOOK_ID));
	}

	private static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> evt) {
		evt.getRegistry().register(ShapedBookRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "shaped_book_recipe")));
		evt.getRegistry().register(ShapelessBookRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "shapeless_book_recipe")));

	}

	public static void init() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(Item.class, PatchouliItems::registerItems);
		modEventBus.addGenericListener(RecipeSerializer.class, PatchouliItems::registerRecipeSerializers);
	}
}
