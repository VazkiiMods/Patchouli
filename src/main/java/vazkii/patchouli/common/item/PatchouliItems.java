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
	public static final Item BOOK = new ItemModBook();

	public static void init() {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addGenericListener(Item.class, PatchouliItems::registerItem);
		bus.addGenericListener(RecipeSerializer.class, PatchouliItems::registerRecipeSerializers);
	}

	private static void registerItem(RegistryEvent.Register<Item> evt) {
		evt.getRegistry().register(BOOK.setRegistryName(BOOK_ID));
	}

	private static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> evt) {
		var r = evt.getRegistry();
		r.register(ShapedBookRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "shaped_book_recipe")));
		r.register(ShapelessBookRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "shapeless_book_recipe")));
	}
}
