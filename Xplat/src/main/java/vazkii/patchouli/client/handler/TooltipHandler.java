package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

public final class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static void onTooltip(GuiGraphicsExtractor graphics, ItemStack stack, int mouseX, int mouseY) {
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

					IClientXplatAbstractions.INSTANCE.submitGuiElement(graphics, new TooltipRenderState(graphics.pose(), cx, cy, angles, r));

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
				graphics.item(lexiconStack, x, tooltipY);
				graphics.itemDecorations(mc.font, lexiconStack, x, tooltipY);
				graphics.pose().popMatrix();

				graphics.pose().pushMatrix();
				//graphics.pose().translate(0, 0, 500);
				graphics.text(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF, true);

				graphics.pose().scale(0.5F, 0.5F);
				boolean mac = Util.getPlatform() == Util.OS.OSX;
				Component key = Component.literal(PatchouliConfig.get().useShiftForQuickLookup() ? "Shift" : mac ? "Cmd" : "Ctrl")
						.withStyle(ChatFormatting.BOLD);
				graphics.text(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF, true);
				graphics.pose().popMatrix();
			} else {
				lexiconLookupTime = 0F;
			}
		} else {
			lexiconLookupTime = 0F;
		}
	}

	private record TooltipRenderState(
			RenderPipeline pipeline,
			TextureSetup textureSetup,
			Matrix3x2f pose,
			int cx,
			int cy,
			float angle,
			float r,
			@Nullable ScreenRectangle bounds,
			@Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {

		TooltipRenderState(Matrix3x2f pose, int cx, int cy, float angle, float r) {
			this(RenderPipelines.SUNRISE_SUNSET, TextureSetup.noTexture(), new Matrix3x2f(pose), cx, cy, angle, r, getBounds((int) (cx - r - 2), (int) (cy - r - 2), (int) (cx + r + 2), (int) (cy + r + 2), pose), null);
		}

		@Override
		public void buildVertices(VertexConsumer buf) {
			float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
			buf.addVertexWith2DPose(pose, cx, cy).setColor(0F, 0.5F, 0F, a);

			for (float i = angle; i > 0; i--) {
				double rad = (i - 90) / 180F * Math.PI;
				buf.addVertexWith2DPose(pose, (float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r)).setColor(0F, 1F, 0F, 1F);
			}

			buf.addVertexWith2DPose(pose, cx, cy).setColor(0F, 1F, 0F, 0F);
		}

		private static ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose) {
			return (new ScreenRectangle(x0, y0, x1 - x0, y1 - y0)).transformMaxBounds(pose);
		}
	}
}
