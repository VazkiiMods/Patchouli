package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.TextFormat;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

public class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static void onTooltip(ItemStack stack, int mouseX, int mouseY) {
		MinecraftClient mc = MinecraftClient.getInstance();
		int tooltipX = mouseX;
		int tooltipY = mouseY - 4;

		if(mc.player != null && !(mc.currentScreen instanceof GuiBook)) {
			int lexSlot = -1;
			ItemStack lexiconStack = ItemStack.EMPTY;
			Pair<BookEntry, Integer> lexiconEntry = null;

			for(int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				ItemStack stackAt = mc.player.inventory.getInvStack(i);
				if(!stackAt.isEmpty()) {
					Book book = BookRightClickHandler.getBookFromStack(stackAt);
					if (book != null) {
						Pair<BookEntry, Integer> entry = book.contents.getEntryForStack(stack);

						if (entry != null && !entry.getFirst().isLocked()) {
							lexiconStack = stackAt;
							lexSlot = i;
							lexiconEntry = entry;
							break;
						}
					}
				}
			}

			if(lexSlot > -1) {
				int x = tooltipX - 34;
				RenderSystem.disableDepthTest();

				DrawableHelper.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
				DrawableHelper.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

				if(PatchouliConfig.useShiftForQuickLookup.get() ? Screen.hasShiftDown() : Screen.hasControlDown()) {
					lexiconLookupTime += ClientTicker.delta;

					int cx = x + 8;
					int cy = tooltipY + 8;
					float r = 12;
					float time = 20F;
					float angles = lexiconLookupTime / time * 360F;

					RenderSystem.disableLighting();
					RenderSystem.disableTexture();
					RenderSystem.shadeModel(GL11.GL_SMOOTH);
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					BufferBuilder buf = Tessellator.getInstance().getBuffer();
					buf.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

					float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
					buf.vertex(cx, cy, 0).color(0F, 0.5F, 0F, a).next();

					for(float i = angles; i > 0; i--) {
						double rad = (i - 90) / 180F * Math.PI;
						buf.vertex(cx + Math.cos(rad) * r, cy + Math.sin(rad) * r, 0).color(0F, 1F, 0F, 1F).next();
					}

					buf.vertex(cx, cy, 0).color(0F, 1F, 0F, 0F).next();
					Tessellator.getInstance().draw();

					RenderSystem.disableBlend();
					RenderSystem.enableTexture();
					RenderSystem.shadeModel(GL11.GL_FLAT);

					if(lexiconLookupTime >= time) {
						mc.player.inventory.selectedSlot = lexSlot;
						int page = lexiconEntry.getSecond();
						ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), page);
					}
				} else lexiconLookupTime = 0F;

				mc.getItemRenderer().zOffset = 300;
				mc.getItemRenderer().renderGuiItem(lexiconStack, x, tooltipY);
				mc.getItemRenderer().zOffset = 0;
				RenderSystem.disableLighting();

				RenderSystem.pushMatrix();
				RenderSystem.translatef(0, 0, 500);
				mc.textRenderer.drawWithShadow("?", x + 10, tooltipY + 8, 0xFFFFFFFF);

				RenderSystem.scalef(0.5F, 0.5F, 1F);
				boolean mac = MinecraftClient.IS_SYSTEM_MAC;
				String key = (PatchouliConfig.useShiftForQuickLookup.get() ? "Shift" : mac ? "Cmd" : "Ctrl");
				mc.textRenderer.drawWithShadow(TextFormat.BOLD + key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
				RenderSystem.popMatrix();

				RenderSystem.enableDepthTest();
			} else lexiconLookupTime = 0F;
		} else lexiconLookupTime = 0F;
	}
}
