package vazkii.patchouli.common.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID)
public class ReloadContentsHandler {
    /*
     We want to reload contents only when all mods are done syncing all of their "datapack stuff" like custom recipes
     Because books could have custom entries that read such recipes
     So use lowest priority and explicitly tell the client when to do it
    */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent evt) {
        NetworkHandler.sendToPlayer(new MessageReloadBookContents(), (ServerPlayerEntity) evt.getPlayer());
        AdvancementSyncHandler.loginSync((ServerPlayerEntity) evt.getPlayer());
    }

    @SubscribeEvent
    public static void serverStart(FMLServerStartingEvent evt) {
        // Also reload contents when someone types /reload
        @SuppressWarnings("deprecation")
        IResourceManagerReloadListener listener = m -> NetworkHandler.sendToAll(new MessageReloadBookContents());
        evt.getServer().getResourceManager().addReloadListener(listener);

        // New advancements could show up, so recompute synced advancements each /reload too
        @SuppressWarnings("deprecation")
        IResourceManagerReloadListener advListener = m -> AdvancementSyncHandler.recomputeSyncedAdvancements(evt.getServer());
        evt.getServer().getResourceManager().addReloadListener(advListener);
    }
}
