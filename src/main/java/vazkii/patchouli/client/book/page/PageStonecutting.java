package vazkii.patchouli.client.book.page;

import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageStonecutting extends PageSimpleProcessingRecipe<StonecuttingRecipe> {

	public PageStonecutting() {
		super(RecipeType.STONECUTTING);
	}
}
