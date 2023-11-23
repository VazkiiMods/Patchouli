package vazkii.patchouli.forge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import vazkii.patchouli.client.book.ClientBookRegistry;

import java.util.function.Supplier;

public class ForgeMessageReloadBookContents {
	public ForgeMessageReloadBookContents() {}

	public static void sendToAll(MinecraftServer server) {
		ForgeNetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeMessageReloadBookContents());
	}

	public void encode(FriendlyByteBuf buf) {}

	public static ForgeMessageReloadBookContents decode(FriendlyByteBuf buf) {
		return new ForgeMessageReloadBookContents();
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientBookRegistry.INSTANCE.reload());
		ctx.get().setPacketHandled(true);
	}
}
