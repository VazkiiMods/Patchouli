package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookConfig extends GuiButtonBook {

	public GuiButtonBookConfig(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 20, 11, 11, onPress,
				Component.translatable("patchouli.gui.lexicon.button.config"));
	}

}
