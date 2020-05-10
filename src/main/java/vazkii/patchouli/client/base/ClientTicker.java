package vazkii.patchouli.client.base;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.gui.screen.Screen;

public final class ClientTicker {

	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;

	private static void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	public static void renderTickStart(float pt) {
		partialTicks = pt;
	}

	public static void renderTickEnd() {
		calcDelta();
	}

	public static void init() {
		ClientTickCallback.EVENT.register(mc -> {
			Screen gui = mc.currentScreen;
			if (gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}

			calcDelta();
		});
	}

}
