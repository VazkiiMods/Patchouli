package vazkii.patchouli.client.book.page.abstr;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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
	protected void drawRecipe(PoseStack ms, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		RenderSystem.setShaderTexture(0, book.craftingTexture);
		RenderSystem.enableBlend();
		GuiComponent.blit(ms, recipeX, recipeY, 11, 71, 96, 24, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getIngredients().get(0));
		parent.renderItemStack(ms, recipeX + 40, recipeY + 4, mouseX, mouseY, recipe.getToastSymbol());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getResultItem(level.registryAccess()));
	}

	@Override
	protected ItemStack getRecipeOutput(T recipe) {
		Level level = Minecraft.getInstance().level;
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getResultItem(level.registryAccess());
	}

	@Override
	protected int getRecipeHeight() {
		return 45;
	}
}
