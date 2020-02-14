package vazkii.patchouli.client.book.gui.button;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBook extends ButtonWidget {

	final GuiBook parent;
	final int u, v;
	final Supplier<Boolean> displayCondition;
	final List<String> tooltip;
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, ButtonWidget.PressAction onPress, String... tooltip) {
		this(parent, x, y, u, v, w, h, ()->true, onPress, tooltip);
	}
	
	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, ButtonWidget.PressAction onPress, String... tooltip) {
		super(x, y, w, h, tooltip[0], onPress);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}
	
	@Override
	public final void render(int mouseX, int mouseY, float partialTicks) {
		active = visible = displayCondition.get();
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		RenderSystem.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(parent.book, x, y, u + (isHovered() ? width : 0), v, width, height);
		if(isHovered)
			parent.setTooltip(getTooltip());
	}

	@Override
    public void playDownSound(SoundManager soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}
	
	public List<String> getTooltip() {
		return tooltip;
	}

}
