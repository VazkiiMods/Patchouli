package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmoking extends PageSimpleProcessingRecipe<SmokingRecipe, FurnaceRecipeDisplay> {

	public PageSmoking() {
		super(RecipeType.SMOKING, FurnaceRecipeDisplay.class);
	}

	@Override
	protected SlotDisplay getIngredient(FurnaceRecipeDisplay display) {
		return display.ingredient();
	}
}
