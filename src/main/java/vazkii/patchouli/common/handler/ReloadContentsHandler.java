package vazkii.patchouli.common.handler;

import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

public class ReloadContentsHandler {
    /*
     We want to reload contents only when all mods are done syncing all of their "datapack stuff" like custom recipes
     Because books could have custom entries that read such recipes
     So use lowest priority and explicitly tell the client when to do it
    */
    public static void playerLogin(ServerPlayerEntity player) {
        MessageReloadBookContents.send(player);
        AdvancementSyncHandler.loginSync(player);
    }

    public static void init() {
        ServerStartCallback.EVENT.register(ReloadContentsHandler::serverStart);
    }

    private static void serverStart(MinecraftServer server) {
        // Also reload contents when someone types /reload
        SynchronousResourceReloadListener listener = m -> MessageReloadBookContents.sendToAll(server);
        server.getDataManager().registerListener(listener);

        // New advancements could show up, so recompute synced advancements each /reload too
        SynchronousResourceReloadListener advListener = m -> AdvancementSyncHandler.recomputeSyncedAdvancements(server);
        server.getDataManager().registerListener(advListener);
    }
}
