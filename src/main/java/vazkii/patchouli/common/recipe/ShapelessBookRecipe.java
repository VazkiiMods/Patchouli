package vazkii.patchouli.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import vazkii.patchouli.common.item.PatchouliItems;

/**
 * Recipe type for shapeless book recipes.
 * The format is the same as vanilla shapeless recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapelessBookRecipe extends BookRecipe<ShapelessRecipe> {

	public ShapelessBookRecipe(ShapelessRecipe compose, ResourceLocation outputBook) {
		super(compose, outputBook);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PatchouliItems.SHAPELESS_BOOK_RECIPE.get();
	}

	public static class Serializer extends WrapperSerializer<ShapelessRecipe, ShapelessBookRecipe> {
		@Override
		protected RecipeSerializer<ShapelessRecipe> getSerializer() {
			return SHAPELESS_RECIPE;
		}

		@Override
		protected ShapelessBookRecipe getRecipe(ShapelessRecipe recipe, ResourceLocation outputBook) {
			return new ShapelessBookRecipe(recipe, outputBook);
		}
	}
}
