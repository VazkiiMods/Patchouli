package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookBack extends GuiButtonBook {

	public GuiButtonBookBack(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, parent::canSeeBackButton, parent::handleButtonBack,
				I18n.translate("patchouli.gui.lexicon.button.back"),
				TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.button.back.info"));
	}

}
