package vazkii.patchouli.client.book.page;

import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageBlasting extends PageSimpleProcessingRecipe<BlastingRecipe> {

	public PageBlasting() {
		super(IRecipeType.BLASTING);
	}
}
