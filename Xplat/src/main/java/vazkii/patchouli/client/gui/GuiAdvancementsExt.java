package vazkii.patchouli.client.gui;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;

import vazkii.patchouli.client.base.ClientAdvancements;

public class GuiAdvancementsExt extends AdvancementsScreen {

	Screen parent;

	public GuiAdvancementsExt(net.minecraft.client.multiplayer.ClientAdvancements manager, Screen parent, Identifier tab) {
		super(manager);
		this.parent = parent;

		AdvancementHolder start = manager.get(tab);
		if (start != null && ClientAdvancements.hasDone(start.id().toString())) {
			manager.setSelectedTab(start, false);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (minecraft.options.keyAdvancements.matches(event) || event.scancode() == 1) {
			minecraft.setScreen(parent);
			return true;
		} else {
			return super.keyPressed(event);
		}
	}
}
