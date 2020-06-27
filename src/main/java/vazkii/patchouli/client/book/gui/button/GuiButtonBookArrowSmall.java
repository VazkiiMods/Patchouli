package vazkii.patchouli.client.book.gui.button;

import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.function.Supplier;

public class GuiButtonBookArrowSmall extends GuiButtonBook {

	public final boolean left;

	public GuiButtonBookArrowSmall(GuiBook parent, int x, int y, boolean left, Supplier<Boolean> displayCondition, IPressable onPress) {
		super(parent, x, y, 272, left ? 27 : 20, 5, 7, displayCondition, onPress,
				new TranslationTextComponent(left ? "patchouli.gui.lexicon.button.prev_page" : "patchouli.gui.lexicon.button.next_page"));
		this.left = left;
	}

}
