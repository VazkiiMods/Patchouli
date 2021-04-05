package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GuiButtonBook extends ButtonWidget {

	final GuiBook parent;
	final int u, v;
	final Supplier<Boolean> displayCondition;
	final List<Text> tooltip;

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, PressAction onPress, Text... tooltip) {
		this(parent, x, y, u, v, w, h, () -> true, onPress, tooltip);
	}

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, PressAction onPress, Text... tooltip) {
		super(x, y, w, h, tooltip[0], onPress);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public final void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		active = visible = displayCondition.get();
		super.render(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, parent.book, x, y, u + (isHovered() ? width : 0), v, width, height);
		if (isHovered()) {
			parent.setTooltip(getTooltip());
		}
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}

	public List<Text> getTooltip() {
		return tooltip;
	}

}
