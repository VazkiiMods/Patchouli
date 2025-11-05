package vazkii.patchouli.client.book.page;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class DummyCraftingInventory implements CraftingContainer, Container {
    public static final DummyCraftingInventory INSTANCE = new DummyCraftingInventory();

    public DummyCraftingInventory() {}

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public net.minecraft.core.@NotNull NonNullList<ItemStack> getItems() {
        return net.minecraft.core.NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return getWidth() * getHeight();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        // NO-OP
    }

    @Override
    public void setChanged() {
        // NO-OP
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        // NO-OP
    }

    @Override
    public void fillStackedContents(@NotNull StackedItemContents stackedItemContents) {
        // NO-OP
    }


}
