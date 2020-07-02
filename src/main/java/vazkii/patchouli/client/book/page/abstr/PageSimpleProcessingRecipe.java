package vazkii.patchouli.client.book.page.abstr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageSimpleProcessingRecipe<T extends IRecipe<?>> extends PageDoubleRecipeRegistry<T> {

	public PageSimpleProcessingRecipe(IRecipeType<T> recipeType) {
		super(recipeType);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		AbstractGui.func_238463_a_(ms, recipeX, recipeY, 11, 71, 96, 24, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getIngredients().get(0));
		parent.renderItemStack(ms, recipeX + 40, recipeY + 4, mouseX, mouseY, recipe.getIcon());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getRecipeOutput());
	}

	@Override
	protected ItemStack getRecipeOutput(T recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getRecipeOutput();
	}

	@Override
	protected int getRecipeHeight() {
		return 45;
	}
}
