package vazkii.patchouli.client.book.page;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.Tuple;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;
import vazkii.patchouli.common.util.ItemStackUtil;

public class PageSmelting extends PageDoubleRecipe<Tuple<ItemStack, ItemStack>> {

    @Override
    protected void drawRecipe(Tuple<ItemStack, ItemStack> recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        mc.renderEngine.bindTexture(book.craftingResource);
        GlStateManager.enableBlend();
        parent.drawModalRectWithCustomSizedTexture(recipeX, recipeY, 11, 71, 96, 24, 128, 128);
        parent.drawCenteredStringNoShadow(getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        renderItem(recipeX + 4, recipeY + 4, mouseX, mouseY, recipe.getFirst());
        renderItem(recipeX + 76, recipeY + 4, mouseX, mouseY, recipe.getSecond());
    }

    @Override
    protected Tuple<ItemStack, ItemStack> loadRecipe(BookEntry entry, String loc) {
        if (loc != null) {
            ItemStack stack = ItemStackUtil.loadStackFromString(loc);
            if (!stack.isEmpty()) {
                ItemStack output = FurnaceRecipes.instance().getSmeltingResult(stack);
                if (!output.isEmpty()) {
                    entry.addRelevantStack(output, pageNum);
                    return new Tuple<>(stack, output);
                }
            }
        }
        return null;
    }

    @Override
    protected ItemStack getRecipeOutput(Tuple<ItemStack, ItemStack> recipe) {
        return recipe.getSecond();
    }

    @Override
    protected int getRecipeHeight() {
        return 45;
    }
}
