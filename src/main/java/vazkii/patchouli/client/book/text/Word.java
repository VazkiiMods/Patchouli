package vazkii.patchouli.client.book.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.List;
import java.util.function.Supplier;

public class Word {
	private final Book book;
	private final GuiBook gui;
	private final int x, y, width, height;
	private final String text;
	private final Style style;
	private final List<Word> linkCluster;
	private final Text tooltip;
	private final Supplier<Boolean> onClick;

	public Word(GuiBook gui, Span span, String text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.text = text;
		this.style = span.style;
		this.onClick = span.onClick;
		this.linkCluster = cluster;
		this.tooltip = span.tooltip;
	}

	public void render(MatrixStack ms, TextRenderer font, Style styleOverride, int mouseX, int mouseY) {
		MutableText toRender = new LiteralText(text)
						.setStyle(style)
						.fillStyle(styleOverride);
		if (isClusterHovered(mouseX, mouseY)) {
			if (onClick != null) {
				toRender.styled(s -> s.withColor(TextColor.fromRgb(book.linkHoverColor)));
			}
			if (!tooltip.getString().isEmpty()) {
				gui.setTooltip(tooltip);
			}
		}

		font.draw(ms, toRender, x, y, -1);
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
