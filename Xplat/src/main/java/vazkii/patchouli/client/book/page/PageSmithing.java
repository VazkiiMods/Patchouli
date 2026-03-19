package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe, SmithingRecipeDisplay> {

	public PageSmithing() {
		super(RecipeType.SMITHING, SmithingRecipeDisplay.class);
	}

	@Override
	protected void drawRecipe(GuiGraphicsExtractor graphics, SmithingRecipeDisplay recipe, ContextMap context, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);
		parent.renderItemStack(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.base().resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 4, recipeY + 23, mouseX, mouseY, recipe.addition().resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, recipe.template().resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 20, mouseX, mouseY, recipe.craftingStation().resolveForFirstStack(context));
		parent.renderItemStack(graphics, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.result().resolveForFirstStack(context));
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
