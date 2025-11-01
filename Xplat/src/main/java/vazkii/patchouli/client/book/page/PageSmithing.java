package vazkii.patchouli.client.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.common.util.RecipeUtil;
import vazkii.patchouli.mixin.AccessorSmithingTransformRecipe;
import vazkii.patchouli.mixin.AccessorSmithingTrimRecipe;

import java.util.Optional;
import java.util.stream.Stream;

public class PageSmithing extends PageDoubleRecipeRegistry<RecipeHolder<SmithingRecipe>> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(GuiGraphics g, RecipeHolder<SmithingRecipe> holder,
							  int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		SmithingRecipe recipe = holder.value();
		Level level = Minecraft.getInstance().level;
		if (level == null) return;

		g.blit(RenderType::guiTextured, book.craftingTexture, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(g, getTitle(second).getVisualOrderText(),
				GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(g, recipeX + 4,  recipeY + 4,  mouseX, mouseY, Optional.ofNullable(getBase(recipe)));
		parent.renderIngredient(g, recipeX + 4,  recipeY + 23, mouseX, mouseY, getAddition(recipe));
		parent.renderIngredient(g, recipeX + 40, recipeY + 4,  mouseX, mouseY, getTemplate(recipe));

		ItemStack out = getRecipeOutput(holder);

		parent.renderItemStack(g, recipeX + 40, recipeY + 20, mouseX, mouseY, out);
		parent.renderItemStack(g, recipeX + 76, recipeY + 13, mouseX, mouseY, out);
	}

	private Ingredient getBase(SmithingRecipe r) {
		if (r instanceof SmithingTrimRecipe t) return ((AccessorSmithingTrimRecipe) t).getBase();
		if (r instanceof SmithingTransformRecipe t) return ((AccessorSmithingTransformRecipe) t).getBase();
		return Ingredient.of(Stream.empty());
	}

	private Optional<Ingredient> getAddition(SmithingRecipe r) {
		if (r instanceof SmithingTrimRecipe t) return Optional.ofNullable(((AccessorSmithingTrimRecipe) t).getAddition());
		if (r instanceof SmithingTransformRecipe t) return ((AccessorSmithingTransformRecipe) t).getAddition();
		return Optional.of(Ingredient.of(Stream.empty()));
	}

	private Optional<Ingredient> getTemplate(SmithingRecipe r) {
		if (r instanceof SmithingTrimRecipe t) return Optional.ofNullable(((AccessorSmithingTrimRecipe) t).getTemplate());
		if (r instanceof SmithingTransformRecipe t) return ((AccessorSmithingTransformRecipe) t).getTemplate();
		return Optional.of(Ingredient.of(Stream.empty()));
	}

	@Override
	protected ItemStack getRecipeOutput(RecipeHolder<SmithingRecipe> holder) {

		SmithingRecipe recipe = holder.value();
		// DummySmithingRecipeInput must wrap base, addition, template
		return recipe.assemble(DummySmithingRecipeInput.INSTANCE, RecipeUtil.getRegistryAccess().orElseThrow());
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
