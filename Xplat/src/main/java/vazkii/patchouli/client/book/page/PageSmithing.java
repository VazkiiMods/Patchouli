package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.mixin.AccessorSmithingTransformRecipe;
import vazkii.patchouli.mixin.AccessorSmithingTrimRecipe;

import java.util.Optional;
import java.util.stream.Stream;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}


		graphics.blit(RenderType::guiTextured, book.craftingTexture, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, Optional.ofNullable(getBase(recipe)));
		parent.renderIngredient(graphics, recipeX + 4, recipeY + 23, mouseX, mouseY, getAddition(recipe));
		parent.renderIngredient(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, getTemplate(recipe));
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 20, mouseX, mouseY, getRecipeOutput(level, recipe));
		parent.renderItemStack(graphics, recipeX + 76, recipeY + 13, mouseX, mouseY, getRecipeOutput(level, recipe));
	}

	private Ingredient getBase(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return ((AccessorSmithingTrimRecipe) recipe).getBase();
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getBase();
		}
		return Ingredient.of(Stream.empty());
	}

	private Optional<Ingredient> getAddition(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return Optional.ofNullable(((AccessorSmithingTrimRecipe) recipe).getAddition());
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getAddition();
		}
		return Optional.of(Ingredient.of(Stream.empty()));
	}

	private Optional<Ingredient> getTemplate(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return Optional.ofNullable(((AccessorSmithingTrimRecipe) recipe).getTemplate());
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getTemplate();
		}
		return Optional.of(Ingredient.of(Stream.empty()));
	}

	@Override
	protected ItemStack getRecipeOutput(Level level, SmithingRecipe recipe) {
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return getRecipeOutput(level, recipe);
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
