package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.common.mixin.MixinSmithingRecipe;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, ((MixinSmithingRecipe) recipe).getBase());
		parent.renderIngredient(ms, recipeX + 4, recipeY + 23, mouseX, mouseY, ((MixinSmithingRecipe) recipe).getAddition());
		parent.renderItemStack(ms, recipeX + 40, recipeY + 13, mouseX, mouseY, recipe.getRecipeKindIcon());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getOutput());
	}

	@Override
	protected ItemStack getRecipeOutput(SmithingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getOutput();
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
