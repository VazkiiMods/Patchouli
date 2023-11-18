package vazkii.patchouli.common.recipe;

import com.google.common.base.Preconditions;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

/**
 * Recipe type for shapeless book recipes.
 * The format is the same as vanilla shapeless recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapelessBookRecipe extends ShapelessRecipe {
	public static final RecipeSerializer<ShapelessBookRecipe> SERIALIZER = new BookRecipeSerializer<>(RecipeSerializer.SHAPELESS_RECIPE, ShapelessBookRecipe::new);

	public ShapelessBookRecipe(ShapelessRecipe compose, @Nullable ResourceLocation outputBook) {
		super(compose.getId(), compose.getGroup(), CraftingBookCategory.MISC, getOutputBook(compose, outputBook), compose.getIngredients());
	}

	private static ItemStack getOutputBook(ShapelessRecipe compose, @Nullable ResourceLocation outputBook) {
		Preconditions.checkArgument(compose.getClass() == ShapelessRecipe.class, "Must be exactly ShapelessRecipe");
		if (outputBook != null) {
			return PatchouliAPI.get().getBookStack(outputBook);
		}
		// The vanilla ShapelessRecipe implementation never uses the passed RegistryAccess, so this is ok.
		return compose.getResultItem(RegistryAccess.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
