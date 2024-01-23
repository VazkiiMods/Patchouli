package vazkii.patchouli.neoforge.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.neoforge.network.handler.NeoForgeClientPayloadHandler;

public class NeoForgeNetworkHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(PatchouliAPI.MOD_ID);
		registrar.play(NeoForgeMessageOpenBookGui.ID, NeoForgeMessageOpenBookGui::new, handler -> handler
				.client(NeoForgeClientPayloadHandler.getInstance()::handleData));
		registrar.play(NeoForgeMessageReloadBookContents.ID, NeoForgeMessageReloadBookContents::new, handler -> handler
				.client(NeoForgeClientPayloadHandler.getInstance()::handleData));
	}
}
