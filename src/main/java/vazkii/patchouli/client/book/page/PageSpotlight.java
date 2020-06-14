package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.minecraft.text.Text;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.util.ItemStackUtil;

public class PageSpotlight extends PageWithText {

	String item, title;
	@SerializedName("link_recipe") boolean linkRecipe;

	transient ItemStack itemStack;

	@Override
	public void build(BookEntry entry, int pageNum) {
		itemStack = ItemStackUtil.loadStackFromString(item);

		if (linkRecipe) {
			entry.addRelevantStack(itemStack, pageNum);
		}
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		mc.getTextureManager().bindTexture(book.craftingTexture);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 128);

		Text toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = itemStack.getName();
		}

		parent.drawCenteredStringNoShadow(ms, toDraw, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		parent.renderItemStack(GuiBook.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, itemStack);

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 40;
	}

}
