package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookBack extends GuiButtonBook {

	public GuiButtonBookBack(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, parent::canSeeBackButton, parent::handleButtonBack,
				new TranslationTextComponent("patchouli.gui.lexicon.button.back"),
				new TranslationTextComponent("patchouli.gui.lexicon.button.back.info").mergeStyle(TextFormatting.GRAY));
	}

}
