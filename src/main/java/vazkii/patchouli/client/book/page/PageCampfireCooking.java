package vazkii.patchouli.client.book.page;

import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageCampfireCooking extends PageSimpleProcessingRecipe<CampfireCookingRecipe> {

	public PageCampfireCooking() {
		super(RecipeType.CAMPFIRE_COOKING);
	}
}
