package vazkii.patchouli.client.book.gui.button;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookBack extends GuiButtonBook {

	public GuiButtonBookBack(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, parent::canSeeBackButton, parent::handleButtonBack,
				new TranslatableText("patchouli.gui.lexicon.button.back"),
				new TranslatableText("patchouli.gui.lexicon.button.back.info").formatted(Formatting.GRAY));
	}

}
