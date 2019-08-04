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
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.UnicodeFontHandler;

@EventBusSubscriber(Dist.CLIENT)
public final class ClientTicker {

	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;
	
	private static boolean requiresBookReload = false;
	
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
			Minecraft mc = Minecraft.getInstance();
			Screen gui = mc.currentScreen;
			if(gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}

			if(mc.world == null)
				requiresBookReload = true;
			else if(requiresBookReload && mc.world.getRecipeManager() != null && mc.world.getRecipeManager().getRecipes().size() > 10) {
				ClientBookRegistry.INSTANCE.reload();
				UnicodeFontHandler.getUnicodeFont(); // early load to prevent visible lag spike
				requiresBookReload = false;
			}
			
			calcDelta();
		}
	}

}