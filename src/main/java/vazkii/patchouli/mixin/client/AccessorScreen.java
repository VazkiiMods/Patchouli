package vazkii.patchouli.mixin.client;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface AccessorScreen {

	@Accessor("children")
	List<GuiEventListener> getChildren();

	@Accessor("narratables")
	List<NarratableEntry> getNarratables();
}
