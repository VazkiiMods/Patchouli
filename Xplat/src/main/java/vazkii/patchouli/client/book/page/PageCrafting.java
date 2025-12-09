package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.*;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

import java.util.List;

public class PageCrafting extends PageDoubleRecipeRegistry<Recipe<?>, RecipeDisplay> {

	public PageCrafting() {
		super(RecipeType.CRAFTING, RecipeDisplay.class);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, RecipeDisplay recipe, ContextMap context, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, book.craftingTexture, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256, 0xffffffff);

		boolean shaped = recipe instanceof ShapedRecipe;
		if (!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			graphics.blit(book.craftingTexture, iconX, iconY, 0, 64, 11, 11, 128, 256);
			if (parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
				parent.setTooltip(Component.translatable("patchouli.gui.lexicon.shapeless"));
			}
		}

		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		int width;
		int height;
		SlotDisplay result;
		List<SlotDisplay> ingredients;
		SlotDisplay craftingStation;
		switch (recipe) {
		case ShapedCraftingRecipeDisplay shapedDisplay -> {
			ingredients = shapedDisplay.ingredients();
			result = shapedDisplay.result();
			craftingStation = shapedDisplay.craftingStation();
			width = shapedDisplay.width();
			height = shapedDisplay.height();
		}
		case ShapelessCraftingRecipeDisplay shapelessDisplay -> {
			ingredients = shapelessDisplay.ingredients();
			result = shapelessDisplay.result();
			craftingStation = shapelessDisplay.craftingStation();
			width = height = 3;
		}
		case null, default -> {
			return;
		}
		}

		PlaceRecipeHelper.placeRecipe(3, 3, width, height, ingredients, (item, slot, x, y) -> {
			parent.renderItemStack(graphics, recipeX + x * 19 + 3, recipeY + y * 19 + 3, mouseX, mouseY, item.resolveForFirstStack(context));
		});
		parent.renderItemStack(graphics, recipeX + 79, recipeY + 22, mouseX, mouseY, result.resolveForFirstStack(context));

		parent.renderItemStack(graphics, recipeX + 79, recipeY + 41, mouseX, mouseY, craftingStation.resolveForFirstStack(context));
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}
}
