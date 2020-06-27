package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GuiButtonBook extends Button {

	final GuiBook parent;
	final int u, v;
	final Supplier<Boolean> displayCondition;
	final List<ITextComponent> tooltip;

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, IPressable onPress, ITextComponent... tooltip) {
		this(parent, x, y, u, v, w, h, () -> true, onPress, tooltip);
	}

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, IPressable onPress, ITextComponent... tooltip) {
		super(x, y, w, h, tooltip[0], onPress);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public final void func_230430_a_(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		field_230693_o_ = field_230694_p_ = displayCondition.get();
		super.func_230430_a_(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	public void func_230431_b_(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, parent.book, field_230690_l_, field_230691_m_, u + (func_230449_g_() ? field_230688_j_ : 0), v, field_230688_j_, field_230689_k_);
		if (func_230449_g_()) {
			parent.setTooltip(getTooltip());
		}
	}

	@Override
	public void func_230988_a_(SoundHandler soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}

	public List<ITextComponent> getTooltip() {
		return tooltip;
	}

}
