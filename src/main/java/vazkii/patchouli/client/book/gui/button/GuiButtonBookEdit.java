package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEdit extends GuiButtonBook {

	public GuiButtonBookEdit(GuiBook parent, int x, int y, Button.IPressable onPress) {
		super(parent, x, y, 308, 9, 11, 11, onPress,
				new TranslationTextComponent("patchouli.gui.lexicon.button.editor"),
				new TranslationTextComponent("patchouli.gui.lexicon.button.editor.info").mergeStyle(TextFormatting.GRAY));
	}

}
