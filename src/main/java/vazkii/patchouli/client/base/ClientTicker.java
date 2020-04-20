package vazkii.patchouli.client.base;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Counts ticks passed in-game, does <b>not</b> stop counting when paused.
 */
@EventBusSubscriber(Dist.CLIENT)
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

	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			partialTicks = event.renderTickTime;
		else calcDelta();
	}

	@SubscribeEvent
	public static void clientTickEnd(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			ticksInGame++;
			partialTicks = 0;

			calcDelta();
		}
	}

}
