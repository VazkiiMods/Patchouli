package vazkii.patchouli.common.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.ClientBookRegistry;

import java.util.function.Supplier;

public class MessageReloadBookContents {
    public MessageReloadBookContents(PacketBuffer buf) {}

    public MessageReloadBookContents() {}

    public void encode(PacketBuffer buf) {

    }

    public boolean receive(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isClient()) {
            context.get().enqueueWork(ClientBookRegistry.INSTANCE::reload);
            return true;
        }
        return false;
    }
}
