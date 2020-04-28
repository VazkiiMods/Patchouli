package vazkii.patchouli.common.handler;

import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageReloadBookContents;

@Mod.EventBusSubscriber(modid = Patchouli.MOD_ID)
public class ReloadContentsHandler {
	@SubscribeEvent
	public static void serverStart(FMLServerStartingEvent evt) {
		// Also reload contents when someone types /reload
		@SuppressWarnings("deprecation")
		IResourceManagerReloadListener listener = m -> NetworkHandler.sendToAll(new MessageReloadBookContents());
		evt.getServer().getResourceManager().addReloadListener(listener);
	}
}
