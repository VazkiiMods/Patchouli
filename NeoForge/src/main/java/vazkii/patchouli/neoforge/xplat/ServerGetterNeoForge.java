package vazkii.patchouli.neoforge.xplat;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import vazkii.patchouli.xplat.ServerGetter;

public final class ServerGetterNeoForge implements ServerGetter {
    public static final ServerGetter INSTANCE = new ServerGetterNeoForge();

    @Override
    public MinecraftServer server() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
