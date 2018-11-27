package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEdit extends GuiButtonBook {

	public GuiButtonBookEdit(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 9, 11, 11,
				I18n.format("patchouli.gui.lexicon.button.editor"),
				TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.button.editor.info"));
	}

}
