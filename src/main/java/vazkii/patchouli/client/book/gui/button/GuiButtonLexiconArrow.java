package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconArrow extends GuiButtonLexicon {

	public final boolean left;
	
	public GuiButtonLexiconArrow(GuiLexicon parent, int x, int y, boolean left) {
		super(parent, x, y, 272, left ? 10 : 0, 18, 10, () -> parent.canSeePageButton(left),
				I18n.translateToLocal(left ? "alquimia.gui.lexicon.button.prev_page" : "alquimia.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
