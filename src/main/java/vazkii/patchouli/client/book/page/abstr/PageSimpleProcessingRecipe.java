package vazkii.patchouli.client.book.page.abstr;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageSimpleProcessingRecipe<T extends Recipe<?>> extends PageDoubleRecipeRegistry<T> {

	public PageSimpleProcessingRecipe(RecipeType<T> recipeType) {
		super(recipeType);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, recipeX, recipeY, 11, 71, 96, 24, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second).asOrderedText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getPreviewInputs().get(0));
		parent.renderItemStack(ms, recipeX + 40, recipeY + 4, mouseX, mouseY, recipe.getRecipeKindIcon());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getOutput());
	}

	@Override
	protected ItemStack getRecipeOutput(T recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getOutput();
	}

	@Override
	protected int getRecipeHeight() {
		return 45;
	}
}
