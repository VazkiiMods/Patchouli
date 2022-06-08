package vazkii.patchouli.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

import java.util.function.BiConsumer;

public class PatchouliItems {

	public static final ResourceLocation BOOK_ID = new ResourceLocation(PatchouliAPI.MOD_ID, "guide_book");
	public static final Item BOOK = new ItemModBook();

	public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
		consumer.accept(BOOK_ID, BOOK);
	}

	public static void submitRecipeSerializerRegistrations(BiConsumer<ResourceLocation, RecipeSerializer<?>> consumer) {
		consumer.accept(new ResourceLocation(PatchouliAPI.MOD_ID, "shaped_book_recipe"), ShapedBookRecipe.SERIALIZER);
		consumer.accept(new ResourceLocation(PatchouliAPI.MOD_ID, "shapeless_book_recipe"), ShapelessBookRecipe.SERIALIZER);
	}
}
