package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, Button.OnPress onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
				Component.translatable("patchouli.gui.lexicon.button.visualize"),
				Component.translatable("patchouli.gui.lexicon.button.visualize.info").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
			graphics.drawString(parent.getMinecraft().font, "!", getX(), getY(), 0xFF3333, true);
		}
	}
}
