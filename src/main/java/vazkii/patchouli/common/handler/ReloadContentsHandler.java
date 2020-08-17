package vazkii.patchouli.common.handler;

import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID)
public class ReloadContentsHandler {
	@SubscribeEvent
	public static void serverStart(FMLServerStartingEvent evt) {
		// Also reload contents when someone types /reload
		@SuppressWarnings("deprecation")
		IResourceManagerReloadListener listener = m -> NetworkHandler.sendToAll(new MessageReloadBookContents());
		Field f = ObfuscationReflectionHelper.findField(MinecraftServer.class, "field_195576_ac");
		try {
			IResourceManager manager = ((DataPackRegistries) f.get(evt.getServer())).getResourceManager();
			((IReloadableResourceManager) manager).addReloadListener(listener);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
