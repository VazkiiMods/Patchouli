package vazkii.patchouli.api.data.page;

import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.data.EntryBuilder;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class CraftingPageBuilder extends RecipePageBuilder {
    public CraftingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("crafting", recipe, entryBuilder);
    }
}
