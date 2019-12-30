package vazkii.patchouli.client.base;

import java.util.ArrayDeque;
import java.util.Queue;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.gui.screen.Screen;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.UnicodeFontHandler;

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

	/* todo fabric
	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			partialTicks = event.renderTickTime;
		else calcDelta();
	}
	*/

	public static void init() {
		ClientTickCallback.EVENT.register(mc -> {
			Screen gui = mc.currentScreen;
			if(gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}

			calcDelta();
		});
	}

}