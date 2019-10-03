package vazkii.patchouli.common.handler;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

@EventBusSubscriber
public final class AdvancementSyncHandler {

	public static Set<String> trackedNamespaces = new HashSet<>();
	
	public static List<ResourceLocation> syncedAdvancements = null;

	@SubscribeEvent
	public static void onAdvancement(AdvancementEvent event) {
		if(event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			buildSyncSet(player);
			
			if(syncedAdvancements.contains(event.getAdvancement().getId()))
				syncPlayer(player, true);
		}
	}
	
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getPlayer() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			buildSyncSet(player);
			syncPlayer(player, false);
		}
	}
	
	private static void buildSyncSet(ServerPlayerEntity player) {
		try {
			if(syncedAdvancements == null) {
				AdvancementManager manager = player.getServer().getAdvancementManager();
				Iterable<Advancement> allAdvancements = manager.getAllAdvancements();
				
				syncedAdvancements = new ArrayList<>();
				for(Advancement a : allAdvancements)
					if(a != null && trackedNamespaces.contains(a.getId().getNamespace()))
						syncedAdvancements.add(a.getId());
			}
		} catch(ConcurrentModificationException e) {
			// Some mods can mess with this in a bad time so in the case it happens we just try it again
			syncedAdvancements = null;
			buildSyncSet(player);
		}
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
