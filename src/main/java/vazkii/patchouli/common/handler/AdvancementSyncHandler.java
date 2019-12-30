package vazkii.patchouli.common.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

public final class AdvancementSyncHandler {

	public static final Set<String> trackedNamespaces = new HashSet<>();

	private static Set<Identifier> syncedAdvancements = Collections.emptySet();

	public static void init() {
		ServerStartCallback.EVENT.register(AdvancementSyncHandler::recomputeSyncedAdvancements);
	}

	public static void recomputeSyncedAdvancements(MinecraftServer server) {
		ServerAdvancementLoader manager = server.getAdvancementLoader();
		syncedAdvancements = manager.getAdvancements().stream()
				.filter(a -> trackedNamespaces.contains(a.getId().getNamespace()))
				.map(Advancement::getId)
				.collect(Collectors.toSet());
	}

	public static void onAdvancement(ServerPlayerEntity player, Advancement adv) {
		if(syncedAdvancements.contains(adv.getId()))
			syncPlayer(player, true);
	}
	
	public static void loginSync(ServerPlayerEntity player) {
		syncPlayer(player, false);
	}

	public static void syncPlayer(ServerPlayerEntity player, boolean showToast) {
		PlayerAdvancementTracker advancements = player.getAdvancementTracker();
		if(advancements == null)
			return;
		
		ServerAdvancementLoader manager = player.getServer().getAdvancementLoader();
		
		List<String> completed = new LinkedList<>();
		for(Identifier res : syncedAdvancements) {
			Advancement adv = manager.get(res);
			if(adv == null)
				continue;
			AdvancementProgress p = advancements.getProgress(adv);
			if(p.isDone())
				completed.add(res.toString());
		}
		
		String[] completedArr = completed.toArray(new String[0]);
		MessageSyncAdvancements.send(player, completedArr, showToast);
	}
	
	
}
