package vazkii.patchouli.client.book.text;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.List;
import java.util.function.Supplier;

/**
 * A {@code Word} is the smallest textual unit of rendering in Patchouli, and knows its
 * position, dimensions, and formatting.
 */
public class Word {
	private final Book book;
	private final GuiBook gui;
	private final ITextComponent text;
	private final List<Word> linkCluster;
	private final Supplier<Boolean> onClick;
	public final int x, y, width, height;

	public Word(GuiBook gui, Span span, IFormattableTextComponent text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.onClick = span.onClick;
		this.linkCluster = cluster;
		if (!span.tooltip.getString().isEmpty()) {
			text = text.modifyStyle(s -> s.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, span.tooltip)));
		}
		this.text = text;
	}

	public void render(MatrixStack ms, FontRenderer font, Style styleOverride, int mouseX, int mouseY) {
		IFormattableTextComponent toRender = text.deepCopy().mergeStyle(styleOverride);
		if (isClusterHovered(mouseX, mouseY)) {
			if (onClick != null) {
				toRender.modifyStyle(s -> s.setColor(Color.fromInt(book.linkHoverColor)));
			}

			gui.renderComponentHoverEffect(ms, text.getStyle(), (int) gui.getRelativeX(mouseX), (int) gui.getRelativeY(mouseY));
		}

		font.func_238422_b_(ms, toRender.func_241878_f(), x, y, -1);
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
