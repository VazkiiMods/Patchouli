package vazkii.patchouli.client.book.page;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;

public final class DummySimpleInventory {
    public static final Container INSTANCE = new SimpleContainer(1);

    private DummySimpleInventory() {}
}
