package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, parent.book.contents.indexIcon, I18n.translate("patchouli.gui.lexicon.index"), onPress);
	}

}
