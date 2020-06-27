package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookConfig extends GuiButtonBook {

	public GuiButtonBookConfig(GuiBook parent, int x, int y, Button.IPressable onPress) {
		super(parent, x, y, 308, 20, 11, 11, onPress,
				new TranslationTextComponent("patchouli.gui.lexicon.button.config"));
	}

}
