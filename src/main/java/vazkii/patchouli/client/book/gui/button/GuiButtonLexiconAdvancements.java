package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconAdvancements extends GuiButtonLexicon {

	public GuiButtonLexiconAdvancements(GuiLexicon parent, int x, int y) {
		super(parent, x, y, 330, 20, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.advancements"));
	}

}
