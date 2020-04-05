package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID, value = Dist.CLIENT)
public class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;

	private static void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			partialTicks = event.renderTickTime;
		else {
			calcDelta();
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent evt) {
		if (evt.phase == TickEvent.Phase.END) {
			Screen gui = Minecraft.getInstance().currentScreen;
			if(gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}
		}
	}

	@SubscribeEvent
	public static void onTooltip(RenderTooltipEvent.PostText evt) {
		Minecraft mc = Minecraft.getInstance();
		int tooltipX = evt.getX();
		int tooltipY = evt.getY() - 4;

		if(mc.player != null && !(mc.currentScreen instanceof GuiBook)) {
			int lexSlot = -1;
			ItemStack lexiconStack = ItemStack.EMPTY;
			Pair<BookEntry, Integer> lexiconEntry = null;

			for(int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				ItemStack stackAt = mc.player.inventory.getStackInSlot(i);
				if(!stackAt.isEmpty()) {
					Book book = BookRightClickHandler.getBookFromStack(stackAt);
					if (book != null) {
						Pair<BookEntry, Integer> entry = book.contents.getEntryForStack(evt.getStack());

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

				AbstractGui.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
				AbstractGui.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

				if(PatchouliConfig.useShiftForQuickLookup.get() ? Screen.hasShiftDown() : Screen.hasControlDown()) {
					lexiconLookupTime += delta;

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
					buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

					float a = 0.5F + 0.2F * ((float) Math.cos(total / 10) * 0.5F + 0.5F);
					buf.vertex(cx, cy, 0).color(0F, 0.5F, 0F, a).endVertex();

					for(float i = angles; i > 0; i--) {
						double rad = (i - 90) / 180F * Math.PI;
						buf.vertex(cx + Math.cos(rad) * r, cy + Math.sin(rad) * r, 0).color(0F, 1F, 0F, 1F).endVertex();
					}

					buf.vertex(cx, cy, 0).color(0F, 1F, 0F, 0F).endVertex();
					Tessellator.getInstance().draw();

					RenderSystem.disableBlend();
					RenderSystem.enableTexture();
					RenderSystem.shadeModel(GL11.GL_FLAT);

					if(lexiconLookupTime >= time) {
						mc.player.inventory.currentItem = lexSlot;
						int page = lexiconEntry.getSecond();
						ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), page);
					}
				} else lexiconLookupTime = 0F;

				mc.getItemRenderer().zLevel = 300;
				mc.getItemRenderer().renderItemAndEffectIntoGUI(lexiconStack, x, tooltipY);
				mc.getItemRenderer().zLevel = 0;
				RenderSystem.disableLighting();

				RenderSystem.pushMatrix();
				RenderSystem.translatef(0, 0, 500);
				mc.fontRenderer.drawStringWithShadow("?", x + 10, tooltipY + 8, 0xFFFFFFFF);

				RenderSystem.scalef(0.5F, 0.5F, 1F);
				boolean mac = Minecraft.IS_RUNNING_ON_MAC;
				String key = (PatchouliConfig.useShiftForQuickLookup.get() ? "Shift" : mac ? "Cmd" : "Ctrl");
				mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
				RenderSystem.popMatrix();

				RenderSystem.enableDepthTest();
			} else lexiconLookupTime = 0F;
		} else lexiconLookupTime = 0F;
	}
}
