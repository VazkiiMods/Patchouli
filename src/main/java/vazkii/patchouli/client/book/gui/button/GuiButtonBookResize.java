package vazkii.patchouli.client.book.gui.button;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;
	
	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				I18n.translate("patchouli.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}
	
	@Override
	public List<String> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale));
	}

}
