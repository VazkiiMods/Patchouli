package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 31, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.visualize"),
				TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.button.visualize.info"));
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		
		if(visible && !PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10)
			mc.fontRenderer.drawStringWithShadow("!", x, y, 0xFF3333);
	}

}
