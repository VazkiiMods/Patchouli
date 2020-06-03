package vazkii.patchouli.common.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import vazkii.patchouli.common.base.Patchouli;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID)
public class PlayerJoinHandler {
	@SubscribeEvent
	public static void playerLogin(PlayerEvent.PlayerLoggedInEvent evt) {
		// Reasoning for this is the same as in 1.14+ versions - vanilla loads advancements on first tick after join.
		EntityPlayerMP player = (EntityPlayerMP) evt.player;
		evt.player.world.getMinecraftServer().getPlayerList().getPlayerAdvancements(player).flushDirty(player);
	}
}
