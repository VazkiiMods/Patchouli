package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiBook parent, int x, int y, Button.IPressable onPress) {
		super(parent, x, y, parent.book.contents.indexIcon, I18n.format("patchouli.gui.lexicon.index"), onPress);
	}

}
