package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageSimpleProcessingRecipe<T extends Recipe<?>> extends PageDoubleRecipeRegistry<T> {

	public PageSimpleProcessingRecipe(RecipeType<T> recipeType) {
		super(recipeType);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

	
		graphics.blit(RenderType::guiTextured,book.craftingTexture, recipeX, recipeY, 11, 71, 96, 24, 128, 256);
		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, null);
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, getRecipeOutput(level, recipe));
		parent.renderItemStack(graphics, recipeX + 76, recipeY + 4, mouseX, mouseY, getRecipeOutput(level, recipe));
	}

	@Override
	protected ItemStack getRecipeOutput(Level level, T recipe) {
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return recipe.assemble(null, level.registryAccess());
	}

	@Override
	protected int getRecipeHeight() {
		return 45;
	}
}
