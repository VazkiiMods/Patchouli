package vazkii.patchouli.forge.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.forge.network.handler.ClientPayloadHandler;

public class ForgeNetworkHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(PatchouliAPI.MOD_ID);
		registrar.play(ForgeMessageOpenBookGui.ID, ForgeMessageOpenBookGui::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleData));
		registrar.play(ForgeMessageReloadBookContents.ID, ForgeMessageReloadBookContents::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleData));
	}
}
