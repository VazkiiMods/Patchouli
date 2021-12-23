package vazkii.patchouli.fabric.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.common.recipe.BookRecipeSerializer;

import java.util.function.BiFunction;

public class FabricRecipeSerializerWrapper<T extends Recipe<?>, U extends T> implements BookRecipeSerializer<T, U> {
	private final RecipeSerializer<T> compose;
	private final BiFunction<T, ResourceLocation, U> converter;

	public FabricRecipeSerializerWrapper(RecipeSerializer<T> compose, BiFunction<T, ResourceLocation, U> converter) {
		this.compose = compose;
		this.converter = converter;
	}

	@Override
	public RecipeSerializer<T> getCompose() {
		return compose;
	}

	@Override
	public BiFunction<T, ResourceLocation, U> getConverter() {
		return converter;
	}
}
