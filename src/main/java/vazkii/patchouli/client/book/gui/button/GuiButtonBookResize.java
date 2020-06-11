package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;

	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				new TranslatableText("patchouli.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}

	@Override
	public List<Text> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				new TranslatableText("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale).formatted(Formatting.GRAY));
	}

}
