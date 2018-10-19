package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconHistory extends GuiButtonLexicon {

	public GuiButtonLexiconHistory(GuiLexicon parent, int x, int y) {
		super(parent, x, y, 330, 31, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.history"));
	}

}
