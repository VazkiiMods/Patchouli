package vazkii.patchouli.common.compat;

import com.bloodnbonesgaming.bnbgamingcore.events.AdvancementVisibilityEvent;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TriumphSyncFixer {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void unhideAdvancement(AdvancementVisibilityEvent event) {
		Advancement advancement = event.getAdvancement();
		if (advancement.getDisplay() == null
				&& event.getAdvancements().getProgress(advancement).isDone()) {
			event.setCanceled(true);
		}
	}
}
