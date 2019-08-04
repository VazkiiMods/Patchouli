package vazkii.patchouli.api;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	public Screen getGui();

	public FontRenderer getFont();

	public void renderItemStack(int x, int y, int mouseX, int mouseY, ItemStack stack);

	public void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	public boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);
	
	public void setHoverTooltip(List<String> tooltip);
	
	public void registerButton(Button button, int pageNum, Runnable onClick);

	public ResourceLocation getBookTexture();
	
	public ResourceLocation getCraftingTexture();
	
	public int getTextColor();
	
	public int getHeaderColor();
	
}
