package vazkii.patchouli.api;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

/**
 * A context for a custom component's methods.
 */
public interface IComponentRenderContext {

	Screen getGui();

	Font getFont();

	void renderItemStack(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, ItemStack stack);

	void renderIngredient(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Optional<Ingredient> ingr);

	boolean isAreaHovered(int mouseX, int mouseY, int x, int y, int w, int h);

	boolean navigateToEntry(ResourceLocation entry, int page, boolean push);

	@Deprecated // use setHoverTooltipComponents
	void setHoverTooltip(List<String> tooltip);

	void setHoverTooltipComponents(List<Component> tooltip);


	void addWidget(AbstractWidget button, int pageNum);

	ResourceLocation getBookTexture();

	ResourceLocation getCraftingTexture();

	int getTextColor();

	int getHeaderColor();

	int getTicksInBook();
}
