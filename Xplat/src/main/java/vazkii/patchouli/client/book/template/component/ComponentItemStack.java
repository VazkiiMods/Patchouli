package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.List;
import java.util.Objects;
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
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		super.onVariablesAvailable(lookup, registries);
		IVariable resolved = lookup.apply(item);

		// The user can provide a list of items (from a JSON array or derivation), or a single item string.
		// We must handle both cases.

		// First, try to parse it as a list.
		List<IVariable> varList = resolved.asList(registries);
		if (varList != null) {
			// It's a list. Stream it, convert each variable to an ItemStack, and collect into an array.
			items = varList.stream()
					.map(v -> v.as(ItemStack.class))
					.filter(Objects::nonNull)
					.toArray(ItemStack[]::new);
		} else {
			// If it's not a list, it must be a single item.
			// Parse it as a single ItemStack.
			ItemStack stack = resolved.as(ItemStack.class);
			if (stack != null) {
				// And wrap it in an array for the component's internal logic.
				items = new ItemStack[] { stack };
			} else {
				// If parsing fails for any reason, default to an empty array to prevent crashes.
				items = new ItemStack[0];
			}
		}
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		if (items.length == 0) {
			return;
		}

		if (framed) {
			int tint = 0xFFFFFFFF;
			graphics.blit(RenderType::guiTextured, page.book.craftingTexture, x - 5, y - 5, 20, 102, 26, 26, 128, 256, tint);
		}

		page.parent.renderItemStack(graphics, x, y, mouseX, mouseY, items[(page.parent.ticksInBook / 20) % items.length]);
	}

}
