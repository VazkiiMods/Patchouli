package vazkii.patchouli.api.data;

import net.minecraft.util.ResourceLocation;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class SmeltingPageBuilder extends RecipePageBuilder {
    public SmeltingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("smelting", recipe, entryBuilder);
    }
}
