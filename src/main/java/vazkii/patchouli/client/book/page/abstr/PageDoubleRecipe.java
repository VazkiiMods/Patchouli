package vazkii.patchouli.client.book.page.abstr;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageDoubleRecipe<T> extends PageWithText {

	@SerializedName("recipe") ResourceLocation recipeId;
	@SerializedName("recipe2") ResourceLocation recipe2Id;
	String title;

	protected transient T recipe1, recipe2;
	protected transient Text title1, title2;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);

		recipe1 = loadRecipe(entry, recipeId);
		recipe2 = loadRecipe(entry, recipe2Id);

		if (recipe1 == null && recipe2 != null) {
			recipe1 = recipe2;
			recipe2 = null;
		}

		boolean customTitle = title != null && !title.isEmpty();
		title1 = !customTitle ? getRecipeOutput(recipe1).getName() : i18nText(title);
		title2 = new LiteralText("-");
		if (recipe2 != null) {
			title2 = !customTitle ? getRecipeOutput(recipe2).getName() : LiteralText.EMPTY;
			if (title1.equals(title2)) {
				title2 = LiteralText.EMPTY;
			}
		}
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		if (recipe1 != null) {
			int recipeX = getX();
			int recipeY = getY();
			drawRecipe(ms, recipe1, recipeX, recipeY, mouseX, mouseY, false);

			if (recipe2 != null) {
				drawRecipe(ms, recipe2, recipeX, recipeY + getRecipeHeight() - (title2.getString().isEmpty() ? 10 : 0), mouseX, mouseY, true);
			}
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return getY() + getRecipeHeight() * (recipe2 == null ? 1 : 2) - (title2.getString().isEmpty() ? 23 : 13);
	}

	@Override
	public boolean shouldRenderText() {
		return getTextHeight() + 10 < GuiBook.PAGE_HEIGHT;
	}

	protected abstract void drawRecipe(MatrixStack ms, T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
	protected abstract T loadRecipe(BookEntry entry, Identifier loc);
	protected abstract ItemStack getRecipeOutput(T recipe);
	protected abstract int getRecipeHeight();

	protected int getX() {
		return GuiBook.PAGE_WIDTH / 2 - 49;
	}

	protected int getY() {
		return 4;
	}

	protected Text getTitle(boolean second) {
		return second ? title2 : title1;
	}

}
