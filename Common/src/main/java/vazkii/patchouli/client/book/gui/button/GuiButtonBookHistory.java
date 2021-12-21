package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookHistory extends GuiButtonBook {

	public GuiButtonBookHistory(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 330, 31, 11, 11, onPress,
				new TranslatableComponent("patchouli.gui.lexicon.button.history"));
	}

}
