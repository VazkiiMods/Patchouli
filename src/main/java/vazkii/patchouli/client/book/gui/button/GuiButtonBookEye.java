package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
				I18n.translate("patchouli.gui.lexicon.button.visualize"),
				TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.button.visualize.info"));
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10)
			parent.getMinecraft().textRenderer.drawWithShadow("!", x, y, 0xFF3333);
	}

}
