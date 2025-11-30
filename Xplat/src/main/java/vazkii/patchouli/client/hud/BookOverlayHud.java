package vazkii.patchouli.client.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public final class BookOverlayHud {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book_overlay");

	private BookOverlayHud() {}

	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		ItemStack bookStack = player.getMainHandItem();
		if (mc.screen == null) {
			Book book = ItemStackUtil.getBookFromStack(bookStack);

			if (book != null) {
				Pair<BookEntry, Integer> hover = BookRightClickHandler.getHoveredEntry(book);
				if (hover != null) {
					BookEntry entry = hover.getFirst();
					if (!entry.isLocked()) {
						Window window = mc.getWindow();
						int x = window.getGuiScaledWidth() / 2 + 3;
						int y = window.getGuiScaledHeight() / 2 + 3;
						entry.getIcon().render(graphics, x, y);

						graphics.pose().pushMatrix();
						graphics.pose().scale(0.5F, 0.5F);
						graphics.renderItem(bookStack, (x + 8) * 2, (y + 8) * 2);
						graphics.renderItemDecorations(mc.font, bookStack, (x + 8) * 2, (y + 8) * 2);
						graphics.pose().popMatrix();

						graphics.drawString(mc.font, entry.getName(), x + 18, y + 3, 0xFFFFFF, false);

						graphics.pose().pushMatrix();
						graphics.pose().scale(0.75F, 0.75F);
						Component s = Component.translatable("patchouli.gui.lexicon." + (player.isShiftKeyDown() ? "view" : "sneak"))
								.withStyle(ChatFormatting.ITALIC);
						graphics.drawString(mc.font, s, (int) ((x + 18) / 0.75F), (int) ((y + 14) / 0.75F), 0xBBBBBB, false);
						graphics.pose().popMatrix();
					}
				}
			}
		}
	}
}
