package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.item.ItemStack;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public abstract class PageDoubleRecipe<T> extends PageWithText {

	String recipe, recipe2, title;

	protected transient T recipeObj1, recipeObj2;
	protected transient String title1, title2;
	
	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		recipeObj1 = loadRecipe(entry, recipe);
		recipeObj2 = loadRecipe(entry, recipe2);
		
		if(recipeObj1 == null && recipeObj2 != null) {
			recipeObj1 = recipeObj2;
			recipeObj2 = null;
		}
		
		boolean customTitle = title != null && !title.isEmpty();
		title1 = !customTitle ? getRecipeOutput(recipeObj1).getDisplayName() : title;
		title2 = "-";
		if(recipeObj2 != null) {
			title2 = !customTitle ? getRecipeOutput(recipeObj2).getDisplayName() : "";
			if(title1.equals(title2))
				title2 = "";
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		if(recipeObj1 != null) {
			int recipeX = getX();	
			int recipeY = getY();
			drawRecipe(recipeObj1, recipeX, recipeY, mouseX, mouseY, false);
			
			if(recipeObj2 != null)
				drawRecipe(recipeObj2, recipeX, recipeY + getRecipeHeight() - (title2.isEmpty() ? 10 : 0), mouseX, mouseY, true);
		}
		
		super.render(mouseX, mouseY, pticks);
	}
	
	@Override
	public int getTextHeight() {
		return getY() + getRecipeHeight() * (recipeObj2 == null ? 1 : 2) - (title2.isEmpty() ? 23 : 13);
	}
	
	@Override
	public boolean shouldRenderText() {
		return getTextHeight() + 10 < GuiLexicon.PAGE_HEIGHT;
	}
	
	protected abstract void drawRecipe(T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
	protected abstract T loadRecipe(BookEntry entry, String loc);
	protected abstract ItemStack getRecipeOutput(T recipe);
	protected abstract int getRecipeHeight();
	
	protected int getX() {
		return GuiLexicon.PAGE_WIDTH / 2 - 49;
	}
	
	protected int getY() {
		return 4;
	}
	
	protected String getTitle(boolean second) {
		return second ? title2 : title1;
	}

}
