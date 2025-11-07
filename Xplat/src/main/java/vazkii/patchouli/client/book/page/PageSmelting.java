package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmelting extends PageSimpleProcessingRecipe<RecipeHolder<SmeltingRecipe>> {

	public PageSmelting() {
		super(RecipeType.SMELTING);
	}

}
