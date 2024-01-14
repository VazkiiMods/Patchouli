package vazkii.patchouli.forge.network.handler;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.forge.network.ForgeMessageOpenBookGui;
import vazkii.patchouli.forge.network.ForgeMessageReloadBookContents;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final ForgeMessageOpenBookGui data, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					ClientBookRegistry.INSTANCE.displayBookGui(data.book(), data.entry(), data.page());
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("patchouli.networking.failed", e.getMessage()));
					return null;
				});
	}

	public void handleData(final ForgeMessageReloadBookContents data, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					ClientBookRegistry.INSTANCE.reload();
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("patchouli.networking.failed", e.getMessage()));
					return null;
				});
	}
}
