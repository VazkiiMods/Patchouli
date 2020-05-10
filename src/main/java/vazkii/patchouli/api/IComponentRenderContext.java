package vazkii.patchouli.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	Screen getGui();

	TextRenderer getFont();

	void renderItemStack(int x, int y, int mouseX, int mouseY, ItemStack stack);

	void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);

	void setHoverTooltip(List<String> tooltip);

	void registerButton(ButtonWidget button, int pageNum, Runnable onClick);

	Identifier getBookTexture();

	Identifier getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
