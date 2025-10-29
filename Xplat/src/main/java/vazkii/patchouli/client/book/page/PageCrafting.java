package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

import java.util.List;
import java.util.Optional;

public class PageCrafting extends PageDoubleRecipeRegistry<Recipe<?>> {

	public PageCrafting() {
		super(RecipeType.CRAFTING);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, Recipe<?> recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}


		graphics.blit(RenderType::guiTextured, book.craftingTexture, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

		boolean shaped = recipe instanceof ShapedRecipe;
		if (!shaped) {
			int iconX = recipeX + 62;
			int iconY = recipeY + 2;
			graphics.blit(RenderType::guiTextured, book.craftingTexture, iconX, iconY, 0, 64, 11, 11, 128, 256);
			if (parent.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
				parent.setTooltip(Component.translatable("patchouli.gui.lexicon.shapeless"));
			}
		}

		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderItemStack(graphics, recipeX + 79, recipeY + 22, mouseX, mouseY, getRecipeOutput(level, recipe));

		List<Ingredient> ingredients = recipe.placementInfo().ingredients();
		int wrap = 3;
		if (shaped) {
			wrap = ((ShapedRecipe) recipe).getWidth();
		}

		for (int i = 0; i < ingredients.size(); i++) {
			parent.renderIngredient(graphics, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, Optional.ofNullable(ingredients.get(i)));
		}

		parent.renderItemStack(graphics, recipeX + 79, recipeY + 41, mouseX, mouseY, getRecipeOutput(level, recipe));
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput(Level level, Recipe<?> recipe) {
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return null;
	}

}
