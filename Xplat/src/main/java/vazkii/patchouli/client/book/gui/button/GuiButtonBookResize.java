package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;

public class GuiButtonBookResize extends GuiButtonBook {
	public GuiButtonBookResize(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				Component.translatable("patchouli.gui.lexicon.button.resize"));
	}

	@Override
	public List<Component> getTooltip() {
		return Arrays.asList(
				tooltip.get(0),
				Component.translatable("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale).withStyle(ChatFormatting.GRAY));
	}

}
