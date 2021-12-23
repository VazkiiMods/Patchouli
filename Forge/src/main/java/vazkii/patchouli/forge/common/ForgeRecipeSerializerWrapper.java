package vazkii.patchouli.forge.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import vazkii.patchouli.common.recipe.BookRecipeSerializer;

import java.util.function.BiFunction;

public class ForgeRecipeSerializerWrapper<T extends Recipe<?>, U extends T> extends ForgeRegistryEntry<RecipeSerializer<?>>
		implements BookRecipeSerializer<T, U> {
	private final RecipeSerializer<T> compose;
	private final BiFunction<T, ResourceLocation, U> converter;

	public ForgeRecipeSerializerWrapper(RecipeSerializer<T> compose, BiFunction<T, ResourceLocation, U> converter) {
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
