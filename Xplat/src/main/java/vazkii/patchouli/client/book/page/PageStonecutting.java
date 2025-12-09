package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageStonecutting extends PageSimpleProcessingRecipe<StonecutterRecipe, StonecutterRecipeDisplay> {

	public PageStonecutting() {
		super(RecipeType.STONECUTTING, StonecutterRecipeDisplay.class);
	}

	@Override
	protected SlotDisplay getIngredient(StonecutterRecipeDisplay display) {
		return display.input();
	}
}
