package vazkii.patchouli.common.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

@EventBusSubscriber
public final class AdvancementSyncHandler {

	public static final Set<String> trackedNamespaces = new HashSet<>();

	private static Set<ResourceLocation> syncedAdvancements = Collections.emptySet();

	@SubscribeEvent
	public static void serverStartedEvent(FMLServerStartedEvent evt) {
		AdvancementManager manager = evt.getServer().getAdvancementManager();
		syncedAdvancements = manager.getAllAdvancements().stream()
				.filter(a -> trackedNamespaces.contains(a.getId().getNamespace()))
				.map(Advancement::getId)
				.collect(Collectors.toSet());
	}

	@SubscribeEvent
	public static void onAdvancement(AdvancementEvent event) {
		if(event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

			if(syncedAdvancements.contains(event.getAdvancement().getId()))
				syncPlayer(player, true);
		}
	}
	
	public static void loginSync(ServerPlayerEntity player) {
		syncPlayer(player, false);
	}

	public static void syncPlayer(ServerPlayerEntity player, boolean showToast) {
		PlayerAdvancements advancements = player.getAdvancements();
		if(advancements == null)
			return;
		
		AdvancementManager manager = player.getServer().getAdvancementManager();
		
		List<String> completed = new LinkedList<>();
		for(ResourceLocation res : syncedAdvancements) {
			Advancement adv = manager.getAdvancement(res);
			if(adv == null)
				continue;
			AdvancementProgress p = advancements.getProgress(adv);
			if(p.isDone())
				completed.add(res.toString());
		}
		
		String[] completedArr = completed.toArray(new String[0]);
		NetworkHandler.sendToPlayer(new MessageSyncAdvancements(completedArr, showToast), player);
	}
	
	
}
