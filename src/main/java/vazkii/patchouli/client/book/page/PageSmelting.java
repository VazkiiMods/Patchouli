package vazkii.patchouli.client.book.page;

import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmelting extends PageSimpleProcessingRecipe<FurnaceRecipe> {

	public PageSmelting() {
		super(IRecipeType.SMELTING);
	}
}
