package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookArrowSmall;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageImage extends PageWithText {

	ResourceLocation[] images;
	String title;
	boolean border;

	transient int index;

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		int x = 90;
		int y = 100;
		addButton(new GuiButtonBookArrowSmall(parent, x, y, true, () -> index > 0, this::handleButtonArrow));
		addButton(new GuiButtonBookArrowSmall(parent, x + 10, y, false, () -> index < images.length - 1, this::handleButtonArrow));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {

		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		graphics.setColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		graphics.pose().scale(0.5F, 0.5F, 0.5F);
		graphics.blit(images[index], x * 2 + 6, y * 2 + 6, 0, 0, 200, 200);
		graphics.pose().scale(2F, 2F, 2F);

		if (border) {
			GuiBook.drawFromTexture(graphics, book, x, y, 405, 149, 106, 106);
		}

		if (title != null && !title.isEmpty()) {
			parent.drawCenteredStringNoShadow(graphics, i18n(title), GuiBook.PAGE_WIDTH / 2, -3, book.headerColor);
		}

		if (images.length > 1 && border) {
			int xs = x + 83;
			int ys = y + 92;
			graphics.fill(xs, ys, xs + 20, ys + 11, 0x44000000);
			graphics.fill(xs - 1, ys - 1, xs + 20, ys + 11, 0x44000000);
		}

		super.render(graphics, mouseX, mouseY, pticks);
	}

	public void handleButtonArrow(Button button) {
		boolean left = ((GuiButtonBookArrowSmall) button).left;
		if (left) {
			index--;
		} else {
			index++;
		}
	}

	@Override
	public int getTextHeight() {
		return 120;
	}

}
