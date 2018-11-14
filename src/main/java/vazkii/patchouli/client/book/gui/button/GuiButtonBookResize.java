package vazkii.patchouli.client.book.gui.button;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;
	
	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale) {
		super(parent, x, y, 330, 9, 11, 11,
				I18n.format("patchouli.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}
	
	@Override
	public List<String> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				TextFormatting.GRAY + I18n.format("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale));
	}

}
