package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageCrafting extends PageDoubleRecipeRegistry<CraftingRecipe> {

	public PageCrafting() {
		super(RecipeType.CRAFTING);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, CraftingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

		boolean shaped = recipe instanceof ShapedRecipe;
		if (!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			DrawableHelper.drawTexture(ms, iconX, iconY, 0, 64, 11, 11, 256, 256);
			if (parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
				parent.setTooltip(new TranslatableText("patchouli.gui.lexicon.shapeless"));
			}
		}

		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderItemStack(ms, recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getOutput());

		DefaultedList<Ingredient> ingredients = recipe.getPreviewInputs();
		int wrap = 3;
		if (shaped) {
			wrap = ((ShapedRecipe) recipe).getWidth();
		}

		for (int i = 0; i < ingredients.size(); i++) {
			parent.renderIngredient(ms, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
		}

		parent.renderItemStack(ms, recipeX + 79, recipeY + 41, mouseX, mouseY, recipe.getRecipeKindIcon());
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput(CraftingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getOutput();
	}

}
