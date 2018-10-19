package vazkii.patchouli.common.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.common.network.NetworkMessage;

public class MessageSyncAdvancements extends NetworkMessage<MessageSyncAdvancements> {

	public String[] done;
	public boolean showToast;
	
	public MessageSyncAdvancements() { }
	
	public MessageSyncAdvancements(String[] done, boolean showToast) { 
		this.done = done;
		this.showToast = showToast;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> {
			ClientAdvancements.setDoneAdvancements(done, showToast);
		});
		return null;
	}
	
}
