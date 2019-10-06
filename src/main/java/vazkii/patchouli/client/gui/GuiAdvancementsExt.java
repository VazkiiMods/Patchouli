package vazkii.patchouli.client.gui;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.base.ClientAdvancements;

public class GuiAdvancementsExt extends AdvancementsScreen {

	Screen parent;
	
	public GuiAdvancementsExt(ClientAdvancementManager manager, Screen parent, String tab) {
		super(manager);
		this.parent = parent;
		
		Advancement start = manager.getAdvancementList().getAdvancement(new ResourceLocation(tab));
		if(start != null && ClientAdvancements.hasDone(start.getId().toString()))
			manager.setSelectedTab(start, false);
	}

	@Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if(key == minecraft.gameSettings.keyBindAdvancements.getKey().getKeyCode() || scanCode == 1) {
        	minecraft.displayGuiScreen(parent);
            return true;
        }
        else return super.keyPressed(key, scanCode, modifiers);
    }
	
}
