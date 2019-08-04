package vazkii.patchouli.client.base;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public final class ClientTicker {

	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;
	
	private static Queue<Runnable> pendingActions = new ArrayDeque<>();

	public static void addAction(Runnable action) {
		pendingActions.add(action);
	}
	
	private static void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent
	public static void renderTick(RenderTickEvent event) {
		if(event.phase == Phase.START)
			partialTicks = event.renderTickTime;
		else calcDelta();
	}

	@SubscribeEvent
	public static void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			Screen gui = Minecraft.getInstance().currentScreen;
			if(gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}
			
			while(!pendingActions.isEmpty())
				pendingActions.poll().run();

			calcDelta();
		}
	}

}