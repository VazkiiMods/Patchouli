package vazkii.patchouli.client.book.page;

import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmelting extends PageSimpleProcessingRecipe<SmeltingRecipe> {

	public PageSmelting() {
		super(RecipeType.SMELTING);
	}
}
