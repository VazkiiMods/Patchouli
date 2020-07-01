package vazkii.patchouli.client.book.page;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmokingRecipe;
import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmoking extends PageSimpleProcessingRecipe<SmokingRecipe> {

	public PageSmoking() {
		super(IRecipeType.SMOKING);
	}
}
