package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, parent.book.getIcon(), new TranslatableComponent("patchouli.gui.lexicon.index"), onPress);
	}

}
