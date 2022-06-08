package vazkii.patchouli.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import vazkii.patchouli.xplat.IXplatAbstractions;

/**
 * Recipe type for shapeless book recipes.
 * The format is the same as vanilla shapeless recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapelessBookRecipe extends ShapelessRecipe {
	public static final RecipeSerializer<ShapelessBookRecipe> SERIALIZER = IXplatAbstractions.INSTANCE.makeWrapperSerializer(RecipeSerializer.SHAPELESS_RECIPE, ShapelessBookRecipe::new);

	public ShapelessBookRecipe(ShapelessRecipe compose, ResourceLocation outputBook) {
		super(compose.getId(), compose.getGroup(), ShapedBookRecipe.getOutputBook(compose, outputBook), compose.getIngredients());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
