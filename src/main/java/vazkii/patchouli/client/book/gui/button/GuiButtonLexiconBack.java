package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconBack extends GuiButtonLexicon {

	public GuiButtonLexiconBack(GuiLexicon parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, () -> parent.canSeeBackButton(),
				I18n.translateToLocal("alquimia.gui.lexicon.button.back"),
				TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.button.back.info"));
	}
	
	
}
