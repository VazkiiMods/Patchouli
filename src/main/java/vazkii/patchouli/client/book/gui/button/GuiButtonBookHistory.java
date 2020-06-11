package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookHistory extends GuiButtonBook {

	public GuiButtonBookHistory(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 330, 31, 11, 11, onPress,
				new TranslatableText("patchouli.gui.lexicon.button.history"));
	}

}
