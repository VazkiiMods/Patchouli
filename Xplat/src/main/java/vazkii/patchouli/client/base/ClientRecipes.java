package vazkii.patchouli.client.base;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ClientRecipes {
	public static final ClientRecipes INSTANCE = new ClientRecipes();

	private final Map<RecipeType<?>, Collection<RecipeHolder<?>>> recipesByType = new HashMap<>();
	private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipesById = new HashMap<>();

	private ClientRecipes() {}

	public void receivedRecipes(Collection<RecipeHolder<?>> recipes) {
		for (RecipeHolder<?> recipe : recipes) {
			recipesByType.computeIfAbsent(recipe.value().getType(), rt -> new java.util.ArrayList<>()).add(recipe);
			recipesById.put(recipe.id(), recipe);
		}
		//ClientBookRegistry.INSTANCE.reload();
	}

	@SuppressWarnings("unchecked")
	public <R extends Recipe<?>> @Nullable RecipeHolder<R> getRecipeById(ResourceKey<Recipe<?>> key) {
		RecipeHolder<?> holder = recipesById.get(key);
		if (holder == null) {
			return null;
		}
		return (RecipeHolder<R>) holder;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends Recipe<?>> Collection<RecipeHolder<? extends T>> getRecipesByType(RecipeType<T> type) {
		return (Collection<RecipeHolder<? extends T>>) (Collection) recipesByType.getOrDefault(type, List.of());
	}

	public <R extends Recipe<?>> @Nullable RecipeHolder<R> getRecipeById(ResourceLocation id) {
		return getRecipeById(ResourceKey.create(Registries.RECIPE, id));
	}
}
