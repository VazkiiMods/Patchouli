package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookBack extends GuiButtonBook {

	public GuiButtonBookBack(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, parent::canSeeBackButton, parent::handleButtonBack,
				new TranslatableComponent("patchouli.gui.lexicon.button.back"),
				new TranslatableComponent("patchouli.gui.lexicon.button.back.info").withStyle(ChatFormatting.GRAY));
	}

}
