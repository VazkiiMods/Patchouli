package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageBlasting extends PageSimpleProcessingRecipe<BlastingRecipe> {

	public PageBlasting() {
		super(RecipeType.BLASTING);
	}
}
