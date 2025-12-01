package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import org.jetbrains.annotations.Nullable;

public class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
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

				graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
				graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

				if (PatchouliConfig.get().useShiftForQuickLookup() ? mc.hasShiftDown() : mc.hasControlDown()) {
					lexiconLookupTime += ClientTicker.delta;

					int cx = x + 8;
					int cy = tooltipY + 8;
					float r = 12;
					float requiredTime = PatchouliConfig.get().quickLookupTime();
					float angles = lexiconLookupTime / requiredTime * 360F;

					IClientXplatAbstractions.INSTANCE.submitGuiElement(graphics, new TooltipRenderState(cx, cy, angles, r));

					if (lexiconLookupTime >= requiredTime) {
						mc.player.getInventory().setSelectedSlot(lexSlot);
						int spread = lexiconEntry.getSecond();
						ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), spread * 2);
					}
				} else {
					lexiconLookupTime = 0F;
				}

				graphics.pose().pushMatrix();
				//graphics.pose().translate(0, 0, 300);
				graphics.renderItem(lexiconStack, x, tooltipY);
				graphics.renderItemDecorations(mc.font, lexiconStack, x, tooltipY);
				graphics.pose().popMatrix();

				graphics.pose().pushMatrix();
				//graphics.pose().translate(0, 0, 500);
				graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF, true);

				graphics.pose().scale(0.5F, 0.5F);
				boolean mac = Util.getPlatform() == Util.OS.OSX;
				Component key = Component.literal(PatchouliConfig.get().useShiftForQuickLookup() ? "Shift" : mac ? "Cmd" : "Ctrl")
						.withStyle(ChatFormatting.BOLD);
				graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF, true);
				graphics.pose().popMatrix();
			} else {
				lexiconLookupTime = 0F;
			}
		} else {
			lexiconLookupTime = 0F;
		}
	}

	private static class TooltipRenderState implements GuiElementRenderState {
		private final RenderPipeline pipeline;
		private final TextureSetup textureSetup;
		private final int cx;
		private final int cy;
		private final float angles;
		private final float r;

		public TooltipRenderState(int cx, int cy, float angles, float r) {
			this.cx = cx;
			this.cy = cy;
			this.angles = angles;
			this.r = r;
			pipeline = RenderPipeline.builder()
					.withBlend(BlendFunction.TRANSLUCENT)
					.build();
			textureSetup = TextureSetup.noTexture();
		}

		@Override
		public void buildVertices(VertexConsumer buf) {
			float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
			buf.addVertex(cx, cy, 0).setColor(0F, 0.5F, 0F, a);

			for (float i = angles; i > 0; i--) {
				double rad = (i - 90) / 180F * Math.PI;
				buf.addVertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).setColor(0F, 1F, 0F, 1F);
			}

			buf.addVertex(cx, cy, 0).setColor(0F, 1F, 0F, 0F);
		}

		@Override
		public RenderPipeline pipeline() {
			return pipeline;
		}

		@Override
		public TextureSetup textureSetup() {
			return textureSetup;
		}

		@Override
		public @Nullable ScreenRectangle scissorArea() {
			return null;
		}

		@Override
		public @Nullable ScreenRectangle bounds() {
			return null;
		}
	}
}
