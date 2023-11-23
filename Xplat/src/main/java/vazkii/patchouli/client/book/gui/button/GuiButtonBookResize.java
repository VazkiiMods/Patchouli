package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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
	public List<Component> getTooltipLines() {
		var size = PersistentData.data.bookGuiScale;
		// The last size with a dedicated message. For sizes 6 and above, we display
		// verybig.message, wrapped in (scale - 5) iterations of verybig.container
		final int lastSize = 5;

		MutableComponent sizeMsg;
		if (size <= lastSize) {
			sizeMsg = Component.translatable("patchouli.gui.lexicon.button.resize.size" + size);
		} else {
			sizeMsg = Component.translatable("patchouli.gui.lexicon.button.resize.verybig.message");
			for (int i = 0; i < size - lastSize; i++) {
				sizeMsg = Component.translatable("patchouli.gui.lexicon.button.resize.verybig.container", sizeMsg);
			}
		}
		return Arrays.asList(tooltip.get(0), sizeMsg.withStyle(ChatFormatting.GRAY));
	}

}
