package vazkii.patchouli.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ISpanState {

	abstract ResourceLocation getBook();

	Style getBaseStyle();

	Deque<Style> getStyleStack();

	abstract IFormattableTextComponent getTooltip();

	void setTooltip(IFormattableTextComponent tooltip);

	abstract Supplier<Boolean> getOnClick();

	abstract List<ISpan> getCluster();

	abstract boolean isExternalLink();

	abstract boolean isEndingExternal();

	abstract int getLineBreaks();

	abstract int getSpacingLeft();

	abstract int getSpacingRight();

	abstract String setOnClick(Supplier<Boolean> onClick);

	abstract String setCluster(List<ISpan> cluster);

	abstract String setExternalLink(boolean externalLink);

	abstract String setEndingExternal(boolean endingExternal);

	abstract String setLineBreaks(int lineBreaks);

	abstract String setSpacingLeft(int spacingLeft);

	abstract void setSpacingRight(int spacingRight);

	String color(Color color);

	String baseColor();

	abstract String modifyStyle(Function<Style, Style> f);

	abstract void pushStyle(Style style);

	abstract Style popStyle();

	abstract void reset();

	abstract Style peekStyle();

	int getLinkColor();

	Minecraft getMinecraft();
}
