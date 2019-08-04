package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

public class PageSmelting extends PageDoubleRecipeRegistry<AbstractCookingRecipe> {

	public PageSmelting() {
		super(AbstractCookingRecipe.class);
	}
	
    @Override
    protected void drawRecipe(AbstractCookingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        mc.textureManager.bindTexture(book.craftingResource);
        GlStateManager.enableBlend();
        AbstractGui.blit(recipeX, recipeY, 11, 71, 96, 24, 128, 128);
        parent.drawCenteredStringNoShadow(getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        parent.renderIngredient(recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getIngredients().get(0));
        parent.renderItemStack(recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getRecipeOutput());
    }


    @Override
    protected ItemStack getRecipeOutput(AbstractCookingRecipe recipe) {
        if (recipe == null)
            return ItemStack.EMPTY;
        
        return recipe.getRecipeOutput();
    }

    @Override
    protected int getRecipeHeight() {
        return 45;
    }
}
