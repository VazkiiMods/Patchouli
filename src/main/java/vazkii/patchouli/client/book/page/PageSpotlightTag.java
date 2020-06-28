package vazkii.patchouli.client.book.page;

import net.fabricmc.fabric.api.tag.TagRegistry;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.util.ItemStackUtil;

import com.google.gson.annotations.SerializedName;

public class PageSpotlightTag extends PageWithText {

	String tag, title;

	transient Ingredient ingredient;

	@Override
	public void build(BookEntry entry, int pageNum) {
		ingredient = Ingredient.fromTag(TagRegistry.item(new Identifier(tag)));
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 256, 256);

		Text toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = i18nText(tag);
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
