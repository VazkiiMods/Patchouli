package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;

	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale, Button.OnPress onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				new TranslatableComponent("patchouli.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}

	@Override
	public List<Component> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				new TranslatableComponent("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale).withStyle(ChatFormatting.GRAY));
	}

}
