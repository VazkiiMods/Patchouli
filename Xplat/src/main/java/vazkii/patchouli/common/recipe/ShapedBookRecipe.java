package vazkii.patchouli.common.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.PatchouliAPI;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends ShapedRecipe {
	public static final RecipeSerializer<ShapedBookRecipe> SERIALIZER = new BookRecipeSerializer<>(RecipeSerializer.SHAPED_RECIPE, ShapedBookRecipe::new);

	public ShapedBookRecipe(ShapedRecipe compose, ResourceLocation outputBook) {
		super(compose.getId(), compose.getGroup(), CraftingBookCategory.MISC, compose.getWidth(), compose.getHeight(), compose.getIngredients(), getOutputBook(compose, outputBook));
	}

	static ItemStack getOutputBook(Recipe<?> compose, ResourceLocation outputBook) {
		Level level = Minecraft.getInstance().level;
		if (level == null)
			return null;
		if (outputBook != null) {
			return PatchouliAPI.get().getBookStack(outputBook);
		}
		return compose.getResultItem(level.registryAccess());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
