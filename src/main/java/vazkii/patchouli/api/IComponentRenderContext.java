package vazkii.patchouli.api;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	Screen getGui();

	FontRenderer getFont();

	void renderItemStack(int x, int y, int mouseX, int mouseY, ItemStack stack);

	void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);

	void setHoverTooltip(List<String> tooltip);

	void registerButton(Button button, int pageNum, Runnable onClick);

	ResourceLocation getBookTexture();

	ResourceLocation getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
