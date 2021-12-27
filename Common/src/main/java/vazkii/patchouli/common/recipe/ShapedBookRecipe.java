package vazkii.patchouli.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.IXplatAbstractions;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends ShapedRecipe {
	public static final RecipeSerializer<ShapedBookRecipe> SERIALIZER = IXplatAbstractions.INSTANCE.makeWrapperSerializer(RecipeSerializer.SHAPED_RECIPE, ShapedBookRecipe::new);

	public ShapedBookRecipe(ShapedRecipe compose, ResourceLocation outputBook) {
		super(compose.getId(), compose.getGroup(), compose.getWidth(), compose.getHeight(), compose.getIngredients(), PatchouliAPI.get().getBookStack(outputBook));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
