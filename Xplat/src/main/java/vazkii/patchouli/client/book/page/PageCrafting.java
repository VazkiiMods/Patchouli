package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.*;

public class PageCrafting extends PageDoubleRecipeRegistry<CraftingRecipe> {

	public PageCrafting() {
		super(RecipeType.CRAFTING);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, CraftingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		
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
		if (recipe instanceof ShapedRecipe){
			List<Optional<Ingredient>> shapedIngredients = ((ShapedRecipe) recipe).getIngredients();
			int wrap = ((ShapedRecipe) recipe).getWidth();

			// Render each ingredient
			for (int i = 0; i < shapedIngredients.size(); i++) {
				Optional<Ingredient> ingredient = shapedIngredients.get(i);
				if (ingredient.isEmpty()) {
					continue;
				}

				int x = recipeX + (i % wrap) * 19 + 3;
				int y = recipeY + (i / wrap) * 19 + 3;
				parent.renderIngredient(graphics, x, y, mouseX, mouseY, (ingredient));
			}
		}
		else { // Shapeless recipe
			List<Ingredient> shapelessIngredients = RecipeUtil.getShapelessIngredients((ShapelessRecipe) recipe);
			for (int i = 0; i < Math.min(shapelessIngredients.size(), 9); i++) {
				Ingredient ingredient = shapelessIngredients.get(i);


				int x = recipeX + (i % 3) * 19 + 3;
				int y = recipeY + (i / 3) * 19 + 3;
				parent.renderIngredient(graphics, x, y, mouseX, mouseY, Optional.of(ingredient));
			}
		}
		ItemStack workstation = WorkstationIcons.iconFor(recipe.getType());
		if (!workstation.isEmpty()) {
			parent.renderItemStack(graphics, recipeX + 79, recipeY + 41, mouseX, mouseY, workstation);
		}
	}
	public static final class WorkstationIcons {

		public static final Map<RecipeType<?>, ItemStack> ICONS = Map.of(
				RecipeType.CRAFTING, new ItemStack(net.minecraft.world.item.Items.CRAFTING_TABLE),
				RecipeType.SMELTING, new ItemStack(net.minecraft.world.item.Items.FURNACE),
				RecipeType.BLASTING, new ItemStack(net.minecraft.world.item.Items.BLAST_FURNACE),
				RecipeType.SMOKING, new ItemStack(net.minecraft.world.item.Items.SMOKER),
				RecipeType.CAMPFIRE_COOKING, new ItemStack(net.minecraft.world.item.Items.CAMPFIRE),
				RecipeType.STONECUTTING, new ItemStack(net.minecraft.world.item.Items.STONECUTTER),
				RecipeType.SMITHING, new ItemStack(net.minecraft.world.item.Items.SMITHING_TABLE)

		);

		public static ItemStack iconFor(RecipeType<?> type) {
			return ICONS.getOrDefault(type, ItemStack.EMPTY);
		}
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

	@Override
	public ItemStack getRecipeOutput(CraftingRecipe recipe) {
		var regs = RecipeUtil.getRegistryAccess().orElse(null);
		if (regs == null) return ItemStack.EMPTY;
		return recipe.assemble(DummyCraftingInventory.INSTANCE.asCraftInput(), regs);
	}
}
