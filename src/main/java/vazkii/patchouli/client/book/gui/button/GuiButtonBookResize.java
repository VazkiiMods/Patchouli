package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;

	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale, Button.IPressable onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				new TranslationTextComponent("patchouli.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}

	@Override
	public List<ITextComponent> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				new TranslationTextComponent("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale).mergeStyle(TextFormatting.GRAY));
	}

}
