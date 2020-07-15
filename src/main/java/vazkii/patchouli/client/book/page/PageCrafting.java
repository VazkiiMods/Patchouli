package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.crafting.IShapedRecipe;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageCrafting extends PageDoubleRecipeRegistry<ICraftingRecipe> {

	public PageCrafting() {
		super(IRecipeType.CRAFTING);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, ICraftingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.textureManager.bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		AbstractGui.blit(ms, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

		boolean shaped = recipe instanceof IShapedRecipe;
		if (!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			AbstractGui.blit(ms, iconX, iconY, 0, 64, 11, 11, 128, 256);
			if (parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
				parent.setTooltip(new TranslationTextComponent("patchouli.gui.lexicon.shapeless"));
			}
		}

		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderItemStack(ms, recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getRecipeOutput());

		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		int wrap = 3;
		if (shaped) {
			wrap = ((IShapedRecipe) recipe).getRecipeWidth();
		}

		for (int i = 0; i < ingredients.size(); i++) {
			parent.renderIngredient(ms, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
		}

		parent.renderItemStack(ms, recipeX + 79, recipeY + 41, mouseX, mouseY, recipe.getIcon());
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput(ICraftingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getRecipeOutput();
	}

}
