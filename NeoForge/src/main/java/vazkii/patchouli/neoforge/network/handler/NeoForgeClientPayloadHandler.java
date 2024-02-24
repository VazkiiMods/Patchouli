package vazkii.patchouli.neoforge.network.handler;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.neoforge.network.NeoForgeMessageOpenBookGui;
import vazkii.patchouli.neoforge.network.NeoForgeMessageReloadBookContents;

public class NeoForgeClientPayloadHandler {
	private static final NeoForgeClientPayloadHandler INSTANCE = new NeoForgeClientPayloadHandler();

	public static NeoForgeClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final NeoForgeMessageOpenBookGui data, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(data.book(), data.entry(), data.page());
		})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("patchouli.networking.open_book.failed", e.getMessage()));
					return null;
				});
	}

	public void handleData(final NeoForgeMessageReloadBookContents data, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
			ClientBookRegistry.INSTANCE.reload();
		})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("patchouli.networking.reload_contents.failed", e.getMessage()));
					return null;
				});
	}
}
