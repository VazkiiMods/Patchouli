package vazkii.patchouli.client.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

/**
 * Counts ticks passed in-game, does <b>not</b> stop counting when paused.
 */
public final class ClientTicker {

	public static long ticksInGame = 0;
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
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
			if (e.phase == TickEvent.Phase.END) {
				ticksInGame++;
				partialTicks = 0;

				calcDelta();
			}
		});
	}

}
