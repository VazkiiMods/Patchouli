package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PageCrafting extends PageDoubleRecipeRegistry<RecipeHolder<CraftingRecipe>> {

	public PageCrafting() {
		super(RecipeType.CRAFTING);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, RecipeHolder<CraftingRecipe> holder, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		CraftingRecipe recipe = holder.value();
		Level level = Minecraft.getInstance().level;
		if (level == null) return;

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

		ItemStack out = getRecipeOutput(recipe);
		parent.renderItemStack(graphics, recipeX + 79, recipeY + 22, mouseX, mouseY, out);

		List<List<ItemStack>> ingredientsList;

		if (recipe instanceof ShapedRecipe shapedRecipe) {
			List<RecipeDisplay> displays = shapedRecipe.display();
			ingredientsList = new ArrayList<>();

			if (!displays.isEmpty() && displays.getFirst() instanceof net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay display) {
				for (SlotDisplay slot : display.ingredients()) {
					if (slot instanceof SlotDisplay.ItemStackSlotDisplay itemSlot) {
						ingredientsList.add(List.of(itemSlot.stack()));
					} else {
						ingredientsList.add(List.of(ItemStack.EMPTY));
					}
				}
			}
		} else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			ingredientsList = RecipeUtil.getShapelessIngredients(shapelessRecipe);
		} else {
			ingredientsList = List.of();
		}

		int wrap = shaped ? ((ShapedRecipe) recipe).getWidth() : 3;

		// Render each ingredient
		for (int i = 0; i < ingredientsList.size(); i++) {
			List<ItemStack> options = ingredientsList.get(i);
			ItemStack stack = options.isEmpty() ? ItemStack.EMPTY : options.getFirst(); // pick first representative
			int x = recipeX + (i % wrap) * 19 + 3;
			int y = recipeY + (i / wrap) * 19 + 3;
			parent.renderIngredient(graphics, x, y, mouseX, mouseY, Optional.of(Ingredient.of(stack.getItem())));
		}

		parent.renderItemStack(graphics, recipeX + 79, recipeY + 41, mouseX, mouseY, out);
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	protected ItemStack getRecipeOutput( RecipeHolder<CraftingRecipe> holder) {
		if (holder == null) return ItemStack.EMPTY;
		return getRecipeOutput(holder.value());
	}

	private ItemStack getRecipeOutput(CraftingRecipe recipe) {
		var regs = RecipeUtil.getRegistryAccess().orElse(null);
		if (regs == null) return ItemStack.EMPTY;
		return recipe.assemble(DummyCraftingInventory.INSTANCE.asCraftInput(), regs);
	}
}
