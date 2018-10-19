package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconEdit extends GuiButtonLexicon {

	public GuiButtonLexiconEdit(GuiLexicon parent, int x, int y) {
		super(parent, x, y, 308, 9, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.editor"),
				TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.button.editor.info"));
	}

}
