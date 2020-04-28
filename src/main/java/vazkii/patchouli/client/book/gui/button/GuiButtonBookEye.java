package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, Button.IPressable onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
				I18n.format("patchouli.gui.lexicon.button.visualize"),
				TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.button.visualize.info"));
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
			parent.getMinecraft().fontRenderer.drawStringWithShadow("!", x, y, 0xFF3333);
		}
	}

}
