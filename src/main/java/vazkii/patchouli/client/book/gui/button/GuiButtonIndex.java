package vazkii.patchouli.client.book.gui.button;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiLexicon parent, int x, int y) {
		super(parent, x, y, new ItemStack(Items.BOOK), I18n.translateToLocal("alquimia.gui.lexicon.index")); // TODO support multiples
	}

}
