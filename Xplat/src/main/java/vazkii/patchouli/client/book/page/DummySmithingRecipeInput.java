package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class DummySmithingRecipeInput  {
    public static final SmithingRecipeInput INSTANCE = new SmithingRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);

    private DummySmithingRecipeInput() {}

}
