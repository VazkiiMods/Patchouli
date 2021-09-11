package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.opengl.GL11;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static void onTooltip(PoseStack ms, ItemStack stack, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		int tooltipX = mouseX;
		int tooltipY = mouseY - 4;

		if (mc.player != null && !(mc.screen instanceof GuiBook)) {
			int lexSlot = -1;
			ItemStack lexiconStack = ItemStack.EMPTY;
			Pair<BookEntry, Integer> lexiconEntry = null;

			for (int i = 0; i < Inventory.getSelectionSize(); i++) {
				ItemStack stackAt = mc.player.getInventory().getItem(i);
				if (!stackAt.isEmpty()) {
					Book book = ItemStackUtil.getBookFromStack(stackAt);
					if (book != null) {
						Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);

						if (entry != null && !entry.getFirst().isLocked()) {
							lexiconStack = stackAt;
							lexSlot = i;
							lexiconEntry = entry;
							break;
						}
					}
				}
			}

			if (lexSlot > -1) {
				int x = tooltipX - 34;
				RenderSystem.disableDepthTest();

				GuiComponent.fill(ms, x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
				GuiComponent.fill(ms, x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

				if (PatchouliConfig.useShiftForQuickLookup.get() ? Screen.hasShiftDown() : Screen.hasControlDown()) {
					lexiconLookupTime += ClientTicker.delta;

					int cx = x + 8;
					int cy = tooltipY + 8;
					float r = 12;
					float time = 20F;
					float angles = lexiconLookupTime / time * 360F;

					//RenderSystem.disableLighting();
					RenderSystem.disableTexture();
					//RenderSystem.shadeModel(GL11.GL_SMOOTH);
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					BufferBuilder buf = Tesselator.getInstance().getBuilder();
					buf.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

					float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
					buf.vertex(cx, cy, 0).color(0F, 0.5F, 0F, a).endVertex();

					for (float i = angles; i > 0; i--) {
						double rad = (i - 90) / 180F * Math.PI;
						buf.vertex(cx + Math.cos(rad) * r, cy + Math.sin(rad) * r, 0).color(0F, 1F, 0F, 1F).endVertex();
					}

					buf.vertex(cx, cy, 0).color(0F, 1F, 0F, 0F).endVertex();
					Tesselator.getInstance().end();

					RenderSystem.disableBlend();
					RenderSystem.enableTexture();
					//RenderSystem.shadeModel(GL11.GL_FLAT);

					if (lexiconLookupTime >= time) {
						mc.player.getInventory().selected = lexSlot;
						int spread = lexiconEntry.getSecond();
						ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().getId(), lexiconEntry.getFirst().getId(), spread * 2);
					}
				} else {
					lexiconLookupTime = 0F;
				}

				mc.getItemRenderer().blitOffset = 300;
				RenderHelper.renderItemStackInGui(ms, lexiconStack, x, tooltipY);
				mc.getItemRenderer().blitOffset = 0;
				//RenderSystem.disableLighting();

				ms.pushPose();
				ms.translate(0, 0, 500);
				mc.font.drawShadow(ms, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);

				ms.scale(0.5F, 0.5F, 1F);
				boolean mac = Minecraft.ON_OSX;
				Component key = new TextComponent(PatchouliConfig.useShiftForQuickLookup.get() ? "Shift" : mac ? "Cmd" : "Ctrl")
						.withStyle(ChatFormatting.BOLD);
				mc.font.drawShadow(ms, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
				ms.popPose();

				RenderSystem.enableDepthTest();
			} else {
				lexiconLookupTime = 0F;
			}
		} else {
			lexiconLookupTime = 0F;
		}
	}
}
