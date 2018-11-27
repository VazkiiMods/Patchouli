package vazkii.patchouli.api;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	public GuiScreen getGui();

	public FontRenderer getFont();

	public void renderItemStack(int x, int y, int mouseX, int mouseY, ItemStack stack);

	public void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	public boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);
	
	public void setHoverTooltip(List<String> tooltip);

}
