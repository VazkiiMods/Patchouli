package vazkii.patchouli.mixin.client;

import java.util.List;

import net.minecraft.client.gui.Selectable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface AccessorScreen {
	
	@Accessor("drawables")
	List<Drawable> getDrawables();

	@Accessor("children")
	List<Drawable> getChildren();

	@Accessor("selectables")
	List<Selectable> getSelectables();
}
