package vazkii.patchouli.api.data.page;

import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.data.EntryBuilder;

public class SmeltingPageBuilder extends RecipePageBuilder<SmeltingPageBuilder> {
	public SmeltingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
		super("smelting", recipe, entryBuilder);
	}
}
