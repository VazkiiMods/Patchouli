package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentItemStack extends TemplateComponent {

	public IVariable item;

	private boolean framed;
	@SerializedName("link_recipe") private boolean linkedRecipe;

	private transient ItemStack[] items;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		if (linkedRecipe) {
			for (ItemStack stack : items) {
				entry.addRelevantStack(builder, stack, pageNum);
			}
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		items = lookup.apply(item).as(ItemStack[].class);
	}

	@Override
	public void render(PoseStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (items.length == 0) {
			return;
		}

		if (framed) {
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

			RenderSystem.setShaderTexture(0, page.book.craftingTexture);
			GuiComponent.blit(ms, x - 5, y - 5, 20, 102, 26, 26, 128, 256);
		}

		page.parent.renderItemStack(ms, x, y, mouseX, mouseY, items[(page.parent.ticksInBook / 20) % items.length]);
	}

}
