package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageEmpty extends BookPage {

	@SerializedName("draw_filler") boolean filler = true;

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pticks) {
		if (filler) {
			GuiBook.drawPageFiller(graphics, book, 0, 0);
		}
	}

}
