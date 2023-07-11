package vazkii.patchouli.common.recipe;

import com.google.common.base.Preconditions;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends ShapedRecipe {
	public static final RecipeSerializer<ShapedBookRecipe> SERIALIZER = new BookRecipeSerializer<>(RecipeSerializer.SHAPED_RECIPE, ShapedBookRecipe::new);

	public ShapedBookRecipe(ShapedRecipe compose, @Nullable ResourceLocation outputBook) {
		super(compose.getId(), compose.getGroup(), CraftingBookCategory.MISC, compose.getWidth(), compose.getHeight(), compose.getIngredients(), getOutputBook(compose, outputBook));
	}

	private static ItemStack getOutputBook(ShapedRecipe compose, @Nullable ResourceLocation outputBook) {
		Preconditions.checkArgument(compose.getClass() == ShapedRecipe.class, "Must be exactly ShapedRecipe");
		if (outputBook != null) {
			return PatchouliAPI.get().getBookStack(outputBook);
		}
		// The vanilla ShapedRecipe implementation never uses the passed RegistryAccess, so this is ok.
		return compose.getResultItem(RegistryAccess.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
