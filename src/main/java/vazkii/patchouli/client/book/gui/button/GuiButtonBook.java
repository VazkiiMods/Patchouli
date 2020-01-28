package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GuiButtonBook extends Button {

	final GuiBook parent;
	final int u, v;
	final Supplier<Boolean> displayCondition;
	final List<String> tooltip;
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, IPressable onPress, String... tooltip) {
		this(parent, x, y, u, v, w, h, ()->true, onPress, tooltip);
	}
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, IPressable onPress, String... tooltip) {
		super(x, y, w, h, "", onPress);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color3f(1F, 1F, 1F);
		active = visible = displayCondition.get();
		isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		
		if(visible) {
			GuiBook.drawFromTexture(parent.book, x, y, u + (isHovered ? width : 0), v, width, height);
			if(isHovered)
				parent.setTooltip(getTooltip());
		}
	}
	
	@Override
    public void playDownSound(SoundHandler soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}
	
	public List<String> getTooltip() {
		return tooltip;
	}

}
