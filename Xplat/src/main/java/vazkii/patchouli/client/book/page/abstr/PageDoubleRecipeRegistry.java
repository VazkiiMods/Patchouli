package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientRecipes;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>, D extends RecipeDisplay> extends PageDoubleRecipe<D> {
	private final RecipeType<? extends T> recipeType;
	private final Class<D> displayClass;

	public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType, Class<D> displayClass) {
		this.recipeType = recipeType;
		this.displayClass = displayClass;
	}

	@Nullable
	private T getRecipe(ResourceLocation id) {
		var recipeHolder = ClientRecipes.INSTANCE.<T>getRecipeById(id);
		return recipeHolder != null && recipeHolder.value().getType() == recipeType ? recipeHolder.value() : null;
	}

	@Override
	protected @Nullable D loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry, ResourceLocation res, boolean linkRecipe) {
		if (res == null || level == null) {
			return null;
		}

		T tempRecipe = getRecipe(res);
		if (tempRecipe == null) { // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = getRecipe(ResourceLocation.fromNamespaceAndPath("crafttweaker", res.getPath()));
		}

		if (tempRecipe == null) {
			PatchouliAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
			return null;
		}

		if (linkRecipe) {
			List<RecipeDisplay> display = tempRecipe.display();
			if (!display.isEmpty()) {
				entry.addRelevantStack(builder, display.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level)), pageNum);
			}
		}

		for (RecipeDisplay recipeDisplay : tempRecipe.display()) {
			if (displayClass.isInstance(recipeDisplay)) {
				return displayClass.cast(recipeDisplay);
			}
		}
		return null;
	}
}
