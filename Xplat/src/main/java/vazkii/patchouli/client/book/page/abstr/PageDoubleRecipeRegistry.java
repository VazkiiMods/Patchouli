package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {
	private final RecipeType<? extends T> recipeType;

	public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
		this.recipeType = recipeType;
	}

	@Nullable
	private T getRecipe(ResourceLocation id) {
		if (Minecraft.getInstance().level == null) {
			return null;
		}
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
		return (T) manager.byKey(id).filter(recipe -> recipe.getType() == recipeType).orElse(null);
	}

	@Override
	protected T loadRecipe(BookContentsBuilder builder, BookEntry entry, ResourceLocation res) {
		if (res == null) {
			return null;
		}

		T tempRecipe = getRecipe(res);
		if (tempRecipe == null) { // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = getRecipe(new ResourceLocation("crafttweaker", res.getPath()));
		}

		if (tempRecipe != null) {
			entry.addRelevantStack(builder, tempRecipe.getResultItem(), pageNum);
			return tempRecipe;
		}

		PatchouliAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
		return null;
	}

}
