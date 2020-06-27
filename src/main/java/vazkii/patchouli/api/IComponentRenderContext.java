package vazkii.patchouli.api;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

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

	void setHoverTooltipComponents(List<ITextComponent> tooltip);

	void registerButton(Button button, int pageNum, Runnable onClick);

	ResourceLocation getBookTexture();

	ResourceLocation getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
