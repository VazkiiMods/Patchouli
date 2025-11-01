package vazkii.patchouli.xplat;

import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

public interface ServerGetter {
    @Nullable
    static ServerGetter get() {
        return PlatformImpl.INSTANCE;
    }

    @Nullable
    MinecraftServer server();
}