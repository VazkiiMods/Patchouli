package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.mixin.AccessorSmithingRecipe;

public class PageSmithing extends PageDoubleRecipeRegistry<UpgradeRecipe> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(PoseStack ms, UpgradeRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		RenderSystem.setShaderTexture(0, book.craftingTexture);
		RenderSystem.enableBlend();
		GuiComponent.blit(ms, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, ((AccessorSmithingRecipe) recipe).getBase());
		parent.renderIngredient(ms, recipeX + 4, recipeY + 23, mouseX, mouseY, ((AccessorSmithingRecipe) recipe).getAddition());
		parent.renderItemStack(ms, recipeX + 40, recipeY + 13, mouseX, mouseY, recipe.getToastSymbol());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getResultItem());
	}

	@Override
	protected ItemStack getRecipeOutput(UpgradeRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getResultItem();
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
