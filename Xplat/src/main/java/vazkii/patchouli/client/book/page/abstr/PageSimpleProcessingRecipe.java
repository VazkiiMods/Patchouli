package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageSimpleProcessingRecipe<T extends Recipe<?>, D extends RecipeDisplay> extends PageDoubleRecipeRegistry<T, D> {
	public PageSimpleProcessingRecipe(RecipeType<T> recipeType, Class<D> displayClass) {
		super(recipeType, displayClass);
	}

	@Override
	protected void drawRecipe(GuiGraphicsExtractor graphics, D recipe, ContextMap context, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX, recipeY, 11, 71, 96, 24, 128, 256, 0xffffffff);
		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		parent.renderItemStack(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, getIngredient(recipe).resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, recipe.craftingStation().resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.result().resolveForFirstStack(context));
	}

	@Override
	protected int getRecipeHeight() {
		return 45;
	}

	protected abstract SlotDisplay getIngredient(D display);
}
