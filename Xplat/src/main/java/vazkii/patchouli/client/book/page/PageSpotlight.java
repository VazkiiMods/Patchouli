package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageSpotlight extends PageWithText {

	IVariable item;
	String title;
	@SerializedName("link_recipe") boolean linkRecipe;

	transient Ingredient ingredient;

	@Override
	public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(level, entry, builder, pageNum);
		ingredient = item.as(Ingredient.class);

		if (linkRecipe) {
			for (ItemStack stack : ingredient.getItems()) {
				entry.addRelevantStack(builder, stack, pageNum);
			}
		}
	}

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		RenderSystem.setShaderTexture(0, book.craftingTexture);
		RenderSystem.enableBlend();
		GuiComponent.blit(ms, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

		Component toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = ingredient.getItems()[0].getHoverName();
		}

		parent.drawCenteredStringNoShadow(ms, toDraw.getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		parent.renderIngredient(ms, GuiBook.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, ingredient);

		super.render(ms, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 40;
	}

}
