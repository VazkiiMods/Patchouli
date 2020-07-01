package vazkii.patchouli.client.book.page;

import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageBlasting extends PageSimpleProcessingRecipe<BlastingRecipe> {

	public PageBlasting() {
		super(RecipeType.BLASTING);
	}
}
