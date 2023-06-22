package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageText extends PageWithText {

	String title;

	public void setText(String text) {
		this.text = IVariable.wrap(text);
	}

	@Override
	public int getTextHeight() {
		if (pageNum == 0) {
			return 22;
		}

		if (title != null && !title.isEmpty()) {
			return 12;
		}

		return -4;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		super.render(graphics, mouseX, mouseY, pticks);

		if (pageNum == 0) {
			boolean renderedSmol = false;
			String smolText = "";

			if (mc.options.advancedItemTooltips) {
				ResourceLocation res = parent.getEntry().getId();
				smolText = res.toString();
			} else if (entry.isExtension()) {
				// TODO 1.20 this needs to be reimplemented
				String name = entry.getTrueProvider().getOwnerName();
				smolText = I18n.get("patchouli.gui.lexicon.added_by", name);
			}

			if (!smolText.isEmpty()) {
				graphics.pose().scale(0.5F, 0.5F, 1F);
				parent.drawCenteredStringNoShadow(graphics, smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor);
				graphics.pose().scale(2F, 2F, 1F);
				renderedSmol = true;
			}

			parent.drawCenteredStringNoShadow(graphics, parent.getEntry().getName().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, renderedSmol ? -3 : 0, book.headerColor);
			GuiBook.drawSeparator(graphics, book, 0, 12);
		} else if (title != null && !title.isEmpty()) {
			parent.drawCenteredStringNoShadow(graphics, i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		}
	}

}
