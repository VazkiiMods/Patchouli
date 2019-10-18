package vazkii.patchouli.common.network.message;

import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.network.IMessage;

public class MessageReloadBookContents implements IMessage {
    public MessageReloadBookContents() {}

    @Override
    public boolean receive(NetworkEvent.Context context) {
        context.enqueueWork(ClientBookRegistry.INSTANCE::reload);
        return true;
    }
}
