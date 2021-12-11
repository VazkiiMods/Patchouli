package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GuiButtonBook extends Button {

	final GuiBook parent;
	final int u, v;
	final Supplier<Boolean> displayCondition;
	final List<Component> tooltip;

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, OnPress onPress, Component... tooltip) {
		this(parent, x, y, u, v, w, h, () -> true, onPress, tooltip);
	}

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, OnPress onPress, Component... tooltip) {
		super(x, y, w, h, tooltip[0], onPress);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public final void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		active = visible = displayCondition.get();
		super.render(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, parent.book, x, y, u + (isHoveredOrFocused() ? width : 0), v, width, height);
		if (isHoveredOrFocused()) {
			parent.setTooltip(getTooltip());
		}
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}

	public List<Component> getTooltip() {
		return tooltip;
	}

}
