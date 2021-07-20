package vazkii.patchouli.common.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends BookRecipe<ShapedRecipe> {
	public static final RecipeSerializer<ShapedBookRecipe> SERIALIZER = new Serializer();

	public ShapedBookRecipe(ShapedRecipe compose, Identifier outputBook) {
		super(compose, outputBook);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer extends WrapperSerializer<ShapedRecipe, ShapedBookRecipe> {
		@Override
		protected RecipeSerializer<ShapedRecipe> getSerializer() {
			return SHAPED;
		}

		@Override
		protected ShapedBookRecipe getRecipe(ShapedRecipe recipe, Identifier outputBook) {
			return new ShapedBookRecipe(recipe, outputBook);
		}
	}
}
