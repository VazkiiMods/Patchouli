package vazkii.patchouli.client.book.page;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class DummySmithingRecipeInput  {
    public static final SmithingRecipeInput INSTANCE = new SmithingRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);

    private DummySmithingRecipeInput() {}

}
