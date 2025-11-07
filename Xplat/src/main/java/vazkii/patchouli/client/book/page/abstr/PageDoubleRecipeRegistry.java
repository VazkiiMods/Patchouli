package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.page.DummyCraftingInventory;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.Optional;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {
	private final RecipeType<?> recipeType;

	public PageDoubleRecipeRegistry(RecipeType<?> recipeType) {
		this.recipeType = recipeType;
	}

	@SuppressWarnings("unchecked")
	private T getRecipe(ResourceLocation id) {
		if (id == null) {
			PatchouliAPI.LOGGER.debug("getRecipe called with null id");
			return null;
		}

		Optional<RecipeHolder<?>> recipeHolder = RecipeUtil.getRecipeByKey(id.getNamespace(), id.getPath());

		if (recipeHolder.isPresent()) {
			RecipeHolder<?> holder = recipeHolder.get();
			if (holder.value().getType() == recipeType) {
				PatchouliAPI.LOGGER.debug("Found recipe {} of type {} (class {})",
						id,
						BuiltInRegistries.RECIPE_TYPE.getKey(recipeType),
						holder.value().getClass().getName()); // Cast is safe here because we check the type
				return (T) holder.value();
			} else {
				PatchouliAPI.LOGGER.warn("Recipe {} found, but its type ({}) does not match expected type ({})",
						id, BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType()), BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
			}

		} else {
			PatchouliAPI.LOGGER.debug("No recipe found for key {}", id);
		}

		return null;
	}

	protected ItemStack getRecipeOutput(T recipe) {
		if (recipe == null) return ItemStack.EMPTY;

		if (recipe instanceof CraftingRecipe crafting) {
			return crafting.assemble(DummyCraftingInventory.INSTANCE.asCraftInput(), RecipeUtil.getRegistryAccess().orElseThrow());
		}

		// Extend with other recipe types as needed, using RecipeUtil.getRegistryAccess() for registry
		return ItemStack.EMPTY;
	}

	@Override
	protected T loadRecipe(BookContentsBuilder builder, BookEntry entry, ResourceLocation res, boolean linkRecipe, int page) {
		if (res == null) {
			PatchouliAPI.LOGGER.debug("loadRecipe called with null res");
			return null;
		}

		T tempRecipe = getRecipe(res);
		boolean usedFallback = false;
		if (tempRecipe == null) {
			ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath("crafttweaker", res.getPath());
			PatchouliAPI.LOGGER.debug("Trying fallback recipe id {}", fallback);
			tempRecipe = getRecipe(fallback);
			usedFallback = tempRecipe != null;
		}

		if (tempRecipe != null) {
			PatchouliAPI.LOGGER.info("Recipe {} found (type {}). Fallback used: {}", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType), usedFallback);
			if (linkRecipe) {
				ItemStack out = getRecipeOutput(tempRecipe);
				entry.addRelevantStack(builder, out, page);
				PatchouliAPI.LOGGER.debug("Linked recipe {} to entry page {} with output {}", res, page, out.isEmpty() ? "EMPTY" : out);
			}
			return tempRecipe;
		}

		PatchouliAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
		return null;
	}
}
