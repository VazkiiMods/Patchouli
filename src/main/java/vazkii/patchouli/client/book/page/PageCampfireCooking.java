package vazkii.patchouli.client.book.page;

import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageCampfireCooking extends PageSimpleProcessingRecipe<CampfireCookingRecipe> {

	public PageCampfireCooking() {
		super(IRecipeType.CAMPFIRE_COOKING);
	}
}
