package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.DummyCraftingInventory;

import vazkii.patchouli.client.book.page.DummySmithingRecipeInput;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.Optional;

public abstract class PageSimpleProcessingRecipe<T extends Recipe<?>> extends PageDoubleRecipeRegistry <T> {
    public PageSimpleProcessingRecipe(RecipeType<? extends Recipe<?>> recipeType) {
        super(recipeType);
    }

    @Override
    protected void drawRecipe(GuiGraphics graphics, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        // keep render safe if no client world present (used only for hover/tooltips etc)
        if (Minecraft.getInstance().level == null) {
            return;
        }

        graphics.blit(RenderType::guiTextured, book.craftingTexture, recipeX, recipeY, 11, 71, 96, 24, 128, 256);
        parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

        // render ingredient. keep the original behaviour but guard against empty display
        try {
            Recipe<?> r2 = recipe;
            Optional<Ingredient> ing = Optional.empty();
            r2.display();// recipe.display() may be a collection of ItemStacks; attempt to build an Ingredient where possible
            ItemStack[] stacks = r2.display().stream().map(ItemStack.class::cast).toArray(ItemStack[]::new);
            if (stacks.length > 0) {
                ing = Optional.of(Ingredient.of());
            }
            parent.renderIngredient(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, ing);
        } catch (Exception ignored) {
            // fall back to no ingredient render on error
        }

        ItemStack out = getRecipeOutput(recipe);
        parent.renderItemStack(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, out);
        parent.renderItemStack(graphics, recipeX + 76, recipeY + 4, mouseX, mouseY, out);
    }
    @SuppressWarnings("deprecation")
    @Override
    protected ItemStack getRecipeOutput(T recipe) {
        if (recipe == null) return ItemStack.EMPTY;
        // get registry access via RecipeUtil
        var regsOpt = RecipeUtil.getRegistryAccess();
        if (regsOpt.isEmpty()) return ItemStack.EMPTY;
        var regs = regsOpt.get();

        Recipe<?> r = recipe;
        return switch (r) {
            case CraftingRecipe craftingRecipe ->
                    craftingRecipe.assemble(DummyCraftingInventory.INSTANCE.asCraftInput(), regs);
            case SmeltingRecipe smeltingRecipe ->
                    smeltingRecipe.assemble(new SingleRecipeInput(smeltingRecipe.input().items().toList().getFirst().value().getDefaultInstance()), regs);
            case BlastingRecipe blastingRecipe ->
                    blastingRecipe.assemble(new SingleRecipeInput(blastingRecipe.input().items().toList().getFirst().value().getDefaultInstance()), regs);
            case SmokingRecipe smokingRecipe ->
                    smokingRecipe.assemble(new SingleRecipeInput(smokingRecipe.input().items().toList().getFirst().value().getDefaultInstance()), regs);
            case CampfireCookingRecipe campfireCookingRecipe ->
                    campfireCookingRecipe.assemble(new SingleRecipeInput(campfireCookingRecipe.input().items().toList().getFirst().value().getDefaultInstance()), regs);
            case StonecutterRecipe stonecutterRecipe ->
                    stonecutterRecipe.assemble(new SingleRecipeInput(stonecutterRecipe.input().items().toList().getFirst().value().getDefaultInstance()), regs);
            case SmithingRecipe smithingRecipe ->
                    smithingRecipe.assemble(DummySmithingRecipeInput.INSTANCE, regs);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    protected int getRecipeHeight() {
        return 45;
    }
}
