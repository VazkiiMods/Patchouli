package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

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

	private static void renderTickStart(float pt) {
		partialTicks = pt;
	}

	private static void renderTickEnd() {
		calcDelta();
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(ClientTicker::onClientTick);
		MinecraftForge.EVENT_BUS.addListener(ClientTicker::onRenderTick);
	}

	public static void onRenderTick(TickEvent.RenderTickEvent evt) {
		switch (evt.phase) {
			case START -> renderTickStart(evt.renderTickTime);
			case END -> renderTickEnd();
		}
	}

	private static void onClientTick(TickEvent.ClientTickEvent e) {
		if (e.phase != TickEvent.Phase.END) {
			return;
		}
		ticksInGame++;
		partialTicks = 0;

		calcDelta();
	}
}
