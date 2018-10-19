package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconConfig extends GuiButtonLexicon {

	public GuiButtonLexiconConfig(GuiLexicon parent, int x, int y) {
		super(parent, x, y, 308, 20, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.config"));
	}

}
