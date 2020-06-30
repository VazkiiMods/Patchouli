package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;

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

	transient List<ItemStack> itemStacks;

	@Override
	public void build(BookEntry entry, int pageNum) {
		itemStacks = (List<ItemStack>) item.as(List.class);

		if (linkRecipe) {
			itemStacks.forEach((stack) -> entry.addRelevantStack(stack, pageNum));
		}
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

		Text toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = itemStacks.get(0).getName();
		}

		parent.drawCenteredStringNoShadow(ms, toDraw, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		parent.renderIngredient(ms, GuiBook.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, Ingredient.ofStacks(itemStacks.toArray(new ItemStack[0])));

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 40;
	}

}
