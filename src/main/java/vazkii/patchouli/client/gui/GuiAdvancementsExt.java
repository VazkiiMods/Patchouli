package vazkii.patchouli.client.gui;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.client.base.ClientAdvancements;

public class GuiAdvancementsExt extends AdvancementsScreen {

	Screen parent;

	public GuiAdvancementsExt(ClientAdvancementManager manager, Screen parent, ResourceLocation tab) {
		super(manager);
		this.parent = parent;

		Advancement start = manager.getAdvancementList().getAdvancement(tab);
		if (start != null && ClientAdvancements.hasDone(start.getId().toString())) {
			manager.setSelectedTab(start, false);
		}
	}

	@Override
	public boolean func_231046_a_(int key, int scanCode, int modifiers) {
		if (getMinecraft().gameSettings.keyBindAdvancements.matchesKey(key, scanCode) || scanCode == 1) {
			getMinecraft().displayGuiScreen(parent);
			return true;
		} else {
			return super.func_231046_a_(key, scanCode, modifiers);
		}
	}

}
