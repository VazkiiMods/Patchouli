package vazkii.patchouli.common.mixin;

import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MixinMinecraftServer {
	@Accessor
	ServerResourceManager getServerResourceManager();
}
