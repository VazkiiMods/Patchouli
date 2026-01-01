package vazkii.patchouli.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

import java.awt.*;

public final class MultiblockProgressHud {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "multiblock_progress");

	private MultiblockProgressHud() {}

	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		MultiblockVisualizationHandler handler = MultiblockVisualizationHandler.INSTANCE;
		if (!handler.hasMultiblock()) {
			return;
		}

		int waitTime = 40;
		int fadeOutSpeed = 4;
		int fullAnimTime = waitTime + 10;
		float animTime = handler.getTimeComplete() + (handler.getTimeComplete() == 0 ? 0 : deltaTracker.getGameTimeDeltaPartialTick(false));

		if (animTime > fullAnimTime) {
			handler.setHasMultiblock(false);
			return;
		}

		graphics.pose().pushMatrix();
		graphics.pose().translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed);

		Minecraft mc = Minecraft.getInstance();
		int x = mc.getWindow().getGuiScaledWidth() / 2;
		int y = 12;

		graphics.drawCenteredString(mc.font, handler.name(), x, y, 0xFFFFFF);

		int width = 180;
		int height = 9;
		int left = x - width / 2;
		int top = y + 10;

		if (handler.getTimeComplete() > 0) {
			graphics.pose().pushMatrix();
			graphics.pose().translate(0, Math.min(height + 5, animTime));
			graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.structure_complete"), x, top + height - 10, 0x00FF00);
			graphics.pose().popMatrix();
		}

		graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
		graphics.fillGradient(left, top, left + width, top + height, 0xFF666666, 0xFF555555);

		float fract = handler.getProgress();
		int progressWidth = (int) ((float) width * fract);
		int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
		int color2 = new Color(color).darker().getRGB();
		graphics.fillGradient(left, top, left + progressWidth, top + height, color, color2);

		if (!handler.isAnchored()) {
			graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.not_anchored"), x, top + height + 8, 0xFFFFFF);
		} else {
			BlockState lookingState = handler.getLookingState();
			if (lookingState != null) {
				// try-catch around here because the state isn't necessarily present in the world in this instance,
				// which isn't really expected behavior for getPickBlock
				try {
					ItemStack stack = lookingState.getCloneItemStack(mc.level, handler.getLookingPos(), false);

					if (!stack.isEmpty()) {
						graphics.drawString(mc.font, stack.getHoverName(), left + 20, top + height + 8, 0xFFFFFF, true);
						graphics.renderItem(stack, left, top + height + 2);
					}
				} catch (Exception ignored) {}
			}

			if (handler.getTimeComplete() == 0) {
				color = 0xFFFFFF;
				int posx = left + width;
				int posy = top + height + 2;
				int mult = 1;
				String progress = handler.getProgressString();

				if (handler.isComplete()) {
					progress = I18n.get("patchouli.gui.lexicon.needs_air");
					color = 0xDA4E3F;
					mult *= 2;
					posx -= width / 2;
					posy += 2;
				}

				graphics.drawString(mc.font, progress, posx - mc.font.width(progress) / mult, posy, color, false);
			}
		}

		graphics.pose().popMatrix();
	}
}
