package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageCampfireCooking extends PageSimpleProcessingRecipe<CampfireCookingRecipe, FurnaceRecipeDisplay> {

	public PageCampfireCooking() {
		super(RecipeType.CAMPFIRE_COOKING, FurnaceRecipeDisplay.class);
	}

	@Override
	protected SlotDisplay getIngredient(FurnaceRecipeDisplay display) {
		return display.ingredient();
	}
}
