package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

	public PageSmithing() {
		super(IRecipeType.field_234827_g_);
	}

	@Override
	protected void drawRecipe(MatrixStack ms, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		AbstractGui.func_238463_a_(ms, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(ms, getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		Ingredient base = ObfuscationReflectionHelper.getPrivateValue(SmithingRecipe.class, recipe, "field_234837_a_");
		Ingredient addition = ObfuscationReflectionHelper.getPrivateValue(SmithingRecipe.class, recipe, "field_234838_b_");
		parent.renderIngredient(ms, recipeX + 4, recipeY + 4, mouseX, mouseY, base);
		parent.renderIngredient(ms, recipeX + 4, recipeY + 23, mouseX, mouseY, addition);
		parent.renderItemStack(ms, recipeX + 40, recipeY + 13, mouseX, mouseY, recipe.getIcon());
		parent.renderItemStack(ms, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getRecipeOutput());
	}

	@Override
	protected ItemStack getRecipeOutput(SmithingRecipe recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getRecipeOutput();
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
