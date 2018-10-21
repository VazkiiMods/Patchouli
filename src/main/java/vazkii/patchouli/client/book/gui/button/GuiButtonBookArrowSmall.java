package vazkii.patchouli.client.book.gui.button;

import com.google.common.base.Supplier;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookArrowSmall extends GuiButtonBook {

	public final boolean left;
	
	public GuiButtonBookArrowSmall(GuiBook parent, int x, int y, boolean left, Supplier<Boolean> displayCondition) {
		super(parent, x, y, 272, left ? 27 : 20, 5, 7, displayCondition,
				I18n.translateToLocal(left ? "patchouli.gui.lexicon.button.prev_page" : "patchouli.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
