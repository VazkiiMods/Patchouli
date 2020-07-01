package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.List;

public class PageSpotlight extends PageWithText {

	IVariable item;
	String title;
	@SerializedName("link_recipe") boolean linkRecipe;

	transient Ingredient ingredient;

	@Override
	public void build(BookEntry entry, int pageNum) {
		ingredient = item.as(Ingredient.class);

		if (linkRecipe) {
			for (ItemStack stack : ingredient.getMatchingStacks()) {
				entry.addRelevantStack(stack, pageNum);
			}
		}
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		mc.textureManager.bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		AbstractGui.func_238463_a_(ms, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

		ITextComponent toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = ingredient.getMatchingStacks()[0].getDisplayName();
		}

		parent.drawCenteredStringNoShadow(ms, toDraw, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		parent.renderIngredient(ms, GuiBook.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, ingredient);

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 40;
	}

}
