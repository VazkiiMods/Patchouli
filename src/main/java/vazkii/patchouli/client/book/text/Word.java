package vazkii.patchouli.client.book.text;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.List;
import java.util.function.Supplier;

public class Word {
	private final Book book;
	private final GuiBook gui;
	private final int x, y, width, height;
	private final String text;
	private final Color color;
	private final String codes;
	private final List<Word> linkCluster;
	private final ITextComponent tooltip;
	private final Supplier<Boolean> onClick;

	public Word(GuiBook gui, Span span, String text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.text = text;
		this.color = span.getColor();
		this.codes = span.getCodes();
		this.onClick = span.getOnClick();
		this.linkCluster = cluster;
		this.tooltip = span.getTooltip();
	}

	public void render(MatrixStack matrixStack, FontRenderer font, float mouseX, float mouseY) {
		String renderTarget = codes + text;
		Color renderColor = color;
		if (isClusterHovered(mouseX, mouseY)) {
			if (onClick != null) {
				renderColor = Color.func_240743_a_(book.linkHoverColor);
			}
			if (!tooltip.getString().isEmpty()) {
				gui.setTooltip(tooltip);
			}
		}

		font.func_238405_a_(matrixStack, renderTarget, x, y, renderColor.func_240742_a_());
	}

	public boolean click(double mouseX, double mouseY, int mouseButton) {
		if (onClick != null && mouseButton == 0 && isHovered(mouseX, mouseY)) {
			return onClick.get();
		}

		return false;
	}

	private boolean isHovered(double mouseX, double mouseY) {
		return gui.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height);
	}

	private boolean isClusterHovered(double mouseX, double mouseY) {
		if (linkCluster == null) {
			return isHovered(mouseX, mouseY);
		}

		for (Word w : linkCluster) {
			if (w.isHovered(mouseX, mouseY)) {
				return true;
			}
		}

		return false;
	}
}
