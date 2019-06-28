package vazkii.patchouli.common.handler;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.concurrent.ConcurrentException;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

public final class AdvancementSyncHandler {

	public static Set<String> trackedNamespaces = new HashSet<>();
	
	public static List<ResourceLocation> syncedAdvancements = null;

	@SubscribeEvent
	public static void onAdvancement(AdvancementEvent event) {
		if(event.getEntityPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
			buildSyncSet(player);
			
			if(syncedAdvancements.contains(event.getAdvancement().getId()))
				syncPlayer(player, true);
		}
	}
	
	@SubscribeEvent
	public static void onLogin(PlayerLoggedInEvent event) {
		if(event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			buildSyncSet(player);
			syncPlayer(player, false);
		}
	}
	
	private static void buildSyncSet(EntityPlayerMP player) {
		try {
			if(syncedAdvancements == null) {
				AdvancementManager manager = player.getServer().getAdvancementManager();
				Iterable<Advancement> allAdvancements = manager.getAdvancements();
				
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
	
	public static void syncPlayer(EntityPlayerMP player, boolean showToast) {
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
		NetworkHandler.INSTANCE.sendTo(new MessageSyncAdvancements(completedArr, showToast), player);
	}
	
	
}
