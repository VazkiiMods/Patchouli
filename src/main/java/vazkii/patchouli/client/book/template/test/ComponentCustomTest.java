package vazkii.patchouli.client.book.template.test;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.common.base.Patchouli;

import java.util.function.Function;

public class ComponentCustomTest implements ICustomComponent {
	private transient int x, y;
	private transient String text = "";

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX;
		y = componentY;
		Patchouli.LOGGER.debug("Custom Component Test built at ({}, {}) page {}", componentX, componentY, pageNum);
	}

	@Override
	public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		ITextComponent toRender = new StringTextComponent(text).func_230530_a_(context.getFont());
		Minecraft.getInstance().fontRenderer.func_238407_a_(ms, toRender, x, y, -1);
	}

	@Override
	public boolean mouseClicked(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton) {
		Patchouli.LOGGER.debug("Custom Component Test clicked at ({}, {}) button {}", mouseX, mouseY, mouseButton);
		return false;
	}

	@Override
	public void onVariablesAvailable(Function<String, IVariable> lookup) {
		text = lookup.apply("First we eat #spaghet#, then we drink #pop#").asString();
	}
}
