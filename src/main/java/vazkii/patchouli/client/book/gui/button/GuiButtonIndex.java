package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiBook parent, int x, int y) {
		super(parent, x, y, new BookIcon(parent.book.getBookItem()), I18n.translateToLocal("patchouli.gui.lexicon.index"));
	}

}
