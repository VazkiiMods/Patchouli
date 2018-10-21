package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookHistory extends GuiButtonBook {

	public GuiButtonBookHistory(GuiBook parent, int x, int y) {
		super(parent, x, y, 330, 31, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.history"));
	}

}
