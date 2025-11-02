package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class DummySingleRecipeInput {
    public static final RecipeInput INSTANCE = new RecipeInput() {
        @Override
        public ItemStack getItem(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    };
}
