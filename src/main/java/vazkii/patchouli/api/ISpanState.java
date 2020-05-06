package vazkii.patchouli.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface ISpanState {
	/*GuiBook getGui();*/

	ResourceLocation getBook();

	FontRenderer getFont();

	int getBaseColor();

	int getColor();

	int getPrevColor();

	String getCodes();

	ITextComponent getTooltip();

	Supplier<Boolean> getOnClick();

	List<ISpan> getCluster();

	boolean isExternalLink();

	boolean isEndingExternal();

	int getLineBreaks();

	int getSpacingLeft();

	int getSpacingRight();

	int getLinkColor();

	Minecraft getMinecraft();

	String setCodes(String codes);

	String setColor(int color);

	String setPrevColor(int prevColor);

	String setTooltip(ITextComponent tooltip);

	String setOnClick(@Nullable Supplier<Boolean> onClick);

	String setCluster(@Nullable List<ISpan> cluster);

	String setExternalLink(boolean externalLink);

	String setEndingExternal(boolean endingExternal);

	String setLineBreaks(int lineBreaks);

	String setSpacingLeft(int spacingLeft);

	String setSpacingRight(int spacingRight);

	void reset();
}
