package vazkii.patchouli.client.book.page;

import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmokingRecipe;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmoking extends PageSimpleProcessingRecipe<SmokingRecipe> {

	public PageSmoking() {
		super(RecipeType.SMOKING);
	}
}
