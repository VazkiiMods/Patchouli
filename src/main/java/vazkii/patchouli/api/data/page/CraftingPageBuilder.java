package vazkii.patchouli.api.data.page;

import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.data.EntryBuilder;

public class CraftingPageBuilder extends RecipePageBuilder<CraftingPageBuilder> {
	public CraftingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
		super("crafting", recipe, entryBuilder);
	}
}
