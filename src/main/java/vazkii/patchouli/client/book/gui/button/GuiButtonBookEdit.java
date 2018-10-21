package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEdit extends GuiButtonBook {

	public GuiButtonBookEdit(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 9, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.editor"),
				TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.button.editor.info"));
	}

}
