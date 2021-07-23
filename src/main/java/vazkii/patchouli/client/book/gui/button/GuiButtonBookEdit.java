package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEdit extends GuiButtonBook {

	public GuiButtonBookEdit(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 9, 11, 11, onPress,
				new TranslatableComponent("patchouli.gui.lexicon.button.editor"),
				new TranslatableComponent("patchouli.gui.lexicon.button.editor.info").withStyle(ChatFormatting.GRAY));
	}

}
