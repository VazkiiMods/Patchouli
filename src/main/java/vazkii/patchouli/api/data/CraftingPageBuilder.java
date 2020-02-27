package vazkii.patchouli.api.data;

import net.minecraft.util.ResourceLocation;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class CraftingPageBuilder extends RecipePageBuilder {
    public CraftingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("crafting", recipe, entryBuilder);
    }
}
