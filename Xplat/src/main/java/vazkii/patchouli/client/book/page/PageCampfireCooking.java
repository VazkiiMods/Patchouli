package vazkii.patchouli.client.book.page;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageCampfireCooking extends PageSimpleProcessingRecipe<RecipeHolder<CampfireCookingRecipe>> {

	public PageCampfireCooking() {
		super(RecipeType.CAMPFIRE_COOKING);
	}


}
