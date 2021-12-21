package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageText extends PageWithText {

	String title;

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
	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {
		super.render(ms, mouseX, mouseY, pticks);

		if (pageNum == 0) {
			boolean renderedSmol = false;
			String smolText = "";

			if (mc.options.advancedItemTooltips) {
				ResourceLocation res = parent.getEntry().getId();
				smolText = res.toString();
			} else if (entry.isExtension()) {
				String name = entry.getTrueProvider().getOwnerName();
				smolText = I18n.get("patchouli.gui.lexicon.added_by", name);
			}

			if (!smolText.isEmpty()) {
				ms.scale(0.5F, 0.5F, 1F);
				parent.drawCenteredStringNoShadow(ms, smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor);
				ms.scale(2F, 2F, 1F);
				renderedSmol = true;
			}

			parent.drawCenteredStringNoShadow(ms, parent.getEntry().getName().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, renderedSmol ? -3 : 0, book.headerColor);
			GuiBook.drawSeparator(ms, book, 0, 12);
		} else if (title != null && !title.isEmpty()) {
			parent.drawCenteredStringNoShadow(ms, i18n(title), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		}
	}

}
