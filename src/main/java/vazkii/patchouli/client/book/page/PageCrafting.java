package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.AbstractGui;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;

public class PageCrafting extends PageDoubleRecipe<IRecipe> {
	@Override
	protected void drawRecipe(IRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.renderEngine.bindTexture(book.craftingResource);
		GlStateManager.enableBlend();
		AbstractGui.drawModalRectWithCustomSizedTexture(recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 128);
		
		boolean shaped = recipe instanceof IShapedRecipe;
		if(!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			AbstractGui.drawModalRectWithCustomSizedTexture(iconX, iconY, 0, 64, 11, 11, 128, 128);
			if(parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11))
				parent.setTooltip(I18n.format("patchouli.gui.lexicon.shapeless"));
		}

		parent.drawCenteredStringNoShadow(getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		
		parent.renderItemStack(recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getRecipeOutput());
		
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		int wrap = 3;
		if(shaped)
			wrap = ((IShapedRecipe) recipe).getRecipeWidth();
		
		for(int i = 0; i < ingredients.size(); i++)
			parent.renderIngredient(recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
	}
	
	protected IRecipe loadRecipe(BookEntry entry, String loc) {
		if(loc == null)
			return null;
		
		ResourceLocation res = new ResourceLocation(loc);
		IRecipe tempRecipe = CraftingManager.getRecipe(res);
		if(tempRecipe == null) // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = CraftingManager.getRecipe(new ResourceLocation("crafttweaker", res.getPath()));
		if(tempRecipe != null)
			entry.addRelevantStack(tempRecipe.getRecipeOutput(), pageNum);
		return tempRecipe;
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput(IRecipe recipe) {
		if (recipe == null)
			return ItemStack.EMPTY;

		return recipe.getRecipeOutput();
	}

}
