package vazkii.patchouli.neoforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import vazkii.patchouli.common.item.ItemModBook;

public class BookCompletionDecoration implements IItemDecorator {
    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        float completion = ItemModBook.getCompletion(stack);
        if (completion < 1) {
            int color = 0x8800FF00;
            int width = Math.round(13.0F * completion);
            guiGraphics.fill(xOffset + 2, yOffset + 13, xOffset + 2 + width, yOffset + 14, color);
        }
        return false;
    }
}
