package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookAdvancements extends GuiButtonBook {

	public GuiButtonBookAdvancements(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 330, 20, 11, 11, onPress,
				I18n.translate("patchouli.gui.lexicon.button.advancements"));
	}

}
