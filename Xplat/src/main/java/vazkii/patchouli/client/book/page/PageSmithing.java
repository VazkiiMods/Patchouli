package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.mixin.AccessorSmithingRecipe;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(PoseStack ms, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		RenderSystem.setShaderTexture(0, book.craftingTexture);
		RenderSystem.enableBlend();
		GuiComponent.blit(ms, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, ((AccessorSmithingRecipe) recipe).getBase());
		parent.renderIngredient(ms, recipeX + 4, recipeY + 23, mouseX, mouseY, ((AccessorSmithingRecipe) recipe).getAddition());
		parent.renderItemStack(ms, recipeX + 40, recipeY + 13, mouseX, mouseY, recipe.getToastSymbol());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getResultItem(level.registryAccess()));
	}

	@Override
	protected ItemStack getRecipeOutput(SmithingRecipe recipe) {
		Level level = Minecraft.getInstance().level;
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getResultItem(level.registryAccess());
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
