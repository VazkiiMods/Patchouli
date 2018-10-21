package vazkii.patchouli.client.book.gui.button;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBook extends GuiButton {

	GuiBook parent;
	int u, v;
	Supplier<Boolean> displayCondition;
	List<String> tooltip;
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, String... tooltip) {
		this(parent, x, y, u, v, w, h, ()->true, tooltip);
	}
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, String... tooltip) {
		super(0, x, y, w, h, "");
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1F, 1F, 1F);
		enabled = visible = displayCondition.get();
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		
		if(visible) {
			GuiBook.drawFromTexture(x, y, u + (hovered ? width : 0), v, width, height);
			if(hovered)
				parent.setTooltip(getTooltip());
		}
	}
	
	@Override
    public void playPressSound(SoundHandler soundHandlerIn) {
		GuiBook.playBookFlipSound();
	}
	
	public List<String> getTooltip() {
		return tooltip;
	}

}
