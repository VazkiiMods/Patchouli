package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.*;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;

import java.util.List;

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

		//RenderSystem.enableBlend();
		graphics.blit(book.craftingTexture, recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

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

		List<RecipeDisplay> display = recipe.display();
		if (display.isEmpty()) {
			return;
		}
		SlotDisplay result;
		List<SlotDisplay> ingredients;
		SlotDisplay craftingStation;
		switch (display.getFirst()) {
		case ShapedCraftingRecipeDisplay shapedDisplay -> {
			ingredients = shapedDisplay.ingredients();
			result = shapedDisplay.result();
			craftingStation = shapedDisplay.craftingStation();
		}
		case ShapelessCraftingRecipeDisplay shapelessDisplay -> {
			ingredients = shapelessDisplay.ingredients();
			result = shapelessDisplay.result();
			craftingStation = shapelessDisplay.craftingStation();
		}
		case null, default -> {
			return;
		}
		}

		ContextMap context = SlotDisplayContext.fromLevel(level);
		PlaceRecipeHelper.placeRecipe(3, 3, recipe, ingredients, (item, slot, x, y) -> {
			parent.renderItemStack(graphics, recipeX + x * 19 + 3, recipeY + y * 19 + 3, mouseX, mouseY, item.resolveForFirstStack(context));
		});
		parent.renderItemStack(graphics, recipeX + 79, recipeY + 22, mouseX, mouseY, result.resolveForFirstStack(context));

		parent.renderItemStack(graphics, recipeX + 79, recipeY + 41, mouseX, mouseY, craftingStation.resolveForFirstStack(context));
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

		List<RecipeDisplay> displays = recipe.display();
		if (displays.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return displays.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
	}

}
