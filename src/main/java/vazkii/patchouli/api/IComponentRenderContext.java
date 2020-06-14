package vazkii.patchouli.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	Screen getGui();

	Style getFont();

	void renderItemStack(MatrixStack ms, int x, int y, int mouseX, int mouseY, ItemStack stack);

	void renderIngredient(MatrixStack ms, int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);

	@Deprecated // use setHoverTooltipComponents
	void setHoverTooltip(List<String> tooltip);

	void setHoverTooltipComponents(List<Text> tooltip);

	void registerButton(ButtonWidget button, int pageNum, Runnable onClick);

	Identifier getBookTexture();

	Identifier getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
