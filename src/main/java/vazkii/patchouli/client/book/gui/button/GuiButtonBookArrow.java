package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookArrow extends GuiButtonBook {

	public final boolean left;

	public GuiButtonBookArrow(GuiBook parent, int x, int y, boolean left) {
		super(parent, x, y, 272, left ? 10 : 0, 18, 10, () -> parent.canSeePageButton(left), parent::handleButtonArrow,
				new TranslationTextComponent(left ? "patchouli.gui.lexicon.button.prev_page" : "patchouli.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
