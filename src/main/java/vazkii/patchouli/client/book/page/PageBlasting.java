package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageBlasting extends PageDoubleRecipeRegistry<BlastingRecipe> {

	public PageBlasting() {
		super(RecipeType.BLASTING);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, BlastingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, recipeX, recipeY, 11, 199, 96, 24, 256, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getPreviewInputs().get(0));
		parent.renderItemStack(ms, recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getOutput());
	}

	@Override
	protected ItemStack getRecipeOutput(BlastingRecipe recipe) {
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
