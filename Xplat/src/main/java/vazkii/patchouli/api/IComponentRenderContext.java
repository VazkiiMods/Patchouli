package vazkii.patchouli.api;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	Screen getGui();

	Style getFont();

	void renderItemStack(PoseStack ms, int x, int y, int mouseX, int mouseY, ItemStack stack);

	void renderIngredient(PoseStack ms, int x, int y, int mouseX, int mouseY, Ingredient ingredient);

	boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);

	@Deprecated // use setHoverTooltipComponents
	void setHoverTooltip(List<String> tooltip);

	void setHoverTooltipComponents(List<Component> tooltip);

	@Deprecated(forRemoval = true) // use addWidget
	void registerButton(Button button, int pageNum, Runnable onClick);

	void addWidget(AbstractWidget button, int pageNum);

	ResourceLocation getBookTexture();

	ResourceLocation getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
