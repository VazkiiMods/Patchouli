package vazkii.patchouli.common.network.message;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.common.network.NetworkMessage;

public class MessageSyncAdvancements extends NetworkMessage<MessageSyncAdvancements> {

	public boolean showToast;
	
	public MessageSyncAdvancements() { }
	
	public MessageSyncAdvancements(boolean showToast) { 
		this.showToast = showToast;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		Minecraft.getMinecraft().addScheduledTask(() ->
				ClientAdvancements.setDoneAdvancements(showToast, false)
		);
		
		return null;
	}
	
}
