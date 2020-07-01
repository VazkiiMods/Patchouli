package vazkii.patchouli.client.book.page;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageStonecutting extends PageSimpleProcessingRecipe<StonecuttingRecipe> {

	public PageStonecutting() {
		super(IRecipeType.STONECUTTING);
	}
}
