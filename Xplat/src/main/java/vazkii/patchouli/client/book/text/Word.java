package vazkii.patchouli.client.book.text;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

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
	private final Component text;
	private final List<Word> linkCluster;
	private final Supplier<Boolean> onClick;
	public final int x, y, width, height;

	public Word(GuiBook gui, Span span, MutableComponent text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.onClick = span.onClick;
		this.linkCluster = cluster;
		if (!span.tooltip.getString().isEmpty()) {
			text = text.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, span.tooltip)));
		}
		this.text = text;
	}

	public void render(PoseStack ms, Font font, Style styleOverride, int mouseX, int mouseY) {
		MutableComponent toRender = text.copy().withStyle(styleOverride);
		if (isClusterHovered(mouseX, mouseY)) {
			if (onClick != null) {
				toRender.withStyle(s -> s.withColor(TextColor.fromRgb(book.linkHoverColor)));
			}

			gui.renderComponentHoverEffect(ms, text.getStyle(), (int) gui.getRelativeX(mouseX), (int) gui.getRelativeY(mouseY));
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
