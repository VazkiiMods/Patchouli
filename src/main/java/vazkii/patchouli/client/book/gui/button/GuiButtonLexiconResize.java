package vazkii.patchouli.client.book.gui.button;

import java.util.List;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import scala.actors.threadpool.Arrays;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconResize extends GuiButtonLexicon {

	final boolean uiscale;
	
	public GuiButtonLexiconResize(GuiLexicon parent, int x, int y, boolean uiscale) {
		super(parent, x, y, 330, 9, 11, 11,
				I18n.translateToLocal("alquimia.gui.lexicon.button.resize"));
		this.uiscale = uiscale;
	}
	
	@Override
	public List<String> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(new String[] { 
				tooltip.get(0),
				TextFormatting.GRAY + I18n.translateToLocalFormatted("alquimia.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale)});
	}

}
