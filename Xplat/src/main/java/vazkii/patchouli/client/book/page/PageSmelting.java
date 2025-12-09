package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmelting extends PageSimpleProcessingRecipe<SmeltingRecipe, FurnaceRecipeDisplay> {

	public PageSmelting() {
		super(RecipeType.SMELTING, FurnaceRecipeDisplay.class);
	}

	@Override
	protected SlotDisplay getIngredient(FurnaceRecipeDisplay display) {
		return display.ingredient();
	}
}
