package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public abstract class PageDoubleRecipeRegistry<T extends IRecipe<?>> extends PageDoubleRecipe<T> {
	private static final Method GET_RECIPE_MAP = ObfuscationReflectionHelper.findMethod(RecipeManager.class, "func_215366_a", IRecipeType.class);
	private final IRecipeType<T> recipeType;
	
	public PageDoubleRecipeRegistry(IRecipeType<T> recipeType) {
		this.recipeType = recipeType;
	}

	@Nullable
	private T getRecipe(ResourceLocation id) {
		RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
		Map<ResourceLocation, T> recipes = Collections.emptyMap();
		try {
			recipes = (Map<ResourceLocation, T>) GET_RECIPE_MAP.invoke(manager, recipeType);
		} catch (IllegalAccessException | InvocationTargetException e) {
			Patchouli.LOGGER.error("Failed to get recipe map", e);
		}
		return recipes.get(id);
	}
	
	@Override
	protected T loadRecipe(BookEntry entry, ResourceLocation res) {
		if(res == null)
			return null;

		T tempRecipe = getRecipe(res);
		if(tempRecipe == null) // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = getRecipe(new ResourceLocation("crafttweaker", res.getPath()));
		
		if(tempRecipe != null) {
			entry.addRelevantStack(tempRecipe.getRecipeOutput(), pageNum);
			return tempRecipe;
		}
		
		return null;
	}
	
}
