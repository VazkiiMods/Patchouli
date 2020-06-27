package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.List;
import java.util.function.Function;

public class ComponentItemStack extends TemplateComponent {

	public String item;

	private boolean framed;
	@SerializedName("link_recipe") private boolean linkedRecipe;

	private transient List<ItemStack> items;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		items = ItemStackUtil.loadStackListFromString(item);
		if (linkedRecipe) {
			for (ItemStack stack : items) {
				entry.addRelevantStack(stack, pageNum);
			}
		}
	}

	@Override
	public void onVariablesAvailable(Function<String, String> lookup) {
		super.onVariablesAvailable(lookup);
		item = lookup.apply(item);
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (items.isEmpty()) {
			return;
		}

		if (framed) {
			RenderSystem.enableBlend();
			RenderSystem.color4f(1F, 1F, 1F, 1F);
			page.mc.textureManager.bindTexture(page.book.craftingTexture);
			AbstractGui.func_238463_a_(ms, x - 4, y - 4, 83, 71, 24, 24, 128, 128);
		}

		page.parent.renderItemStack(ms, x, y, mouseX, mouseY, items.get((page.parent.ticksInBook / 20) % items.size()));
	}

}
