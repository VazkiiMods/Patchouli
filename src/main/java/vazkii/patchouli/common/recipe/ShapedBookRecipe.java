package vazkii.patchouli.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import vazkii.patchouli.common.item.PatchouliItems;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends BookRecipe<ShapedRecipe> {

	public ShapedBookRecipe(ShapedRecipe compose, ResourceLocation outputBook) {
		super(compose, outputBook);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PatchouliItems.SHAPED_BOOK_RECIPE.get();
	}

	public static class Serializer extends WrapperSerializer<ShapedRecipe, ShapedBookRecipe> {
		@Override
		protected RecipeSerializer<ShapedRecipe> getSerializer() {
			return SHAPED_RECIPE;
		}

		@Override
		protected ShapedBookRecipe getRecipe(ShapedRecipe recipe, ResourceLocation outputBook) {
			return new ShapedBookRecipe(recipe, outputBook);
		}
	}
}
