package vazkii.patchouli.common.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.network.NetworkMessage;

public class MessageOpenBookGui extends NetworkMessage<MessageOpenBookGui> {

	public String book;
	
	public MessageOpenBookGui() { }
	
	public MessageOpenBookGui(String book) { 
		this.book = book;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(book);
		});
		
		return null;
	}

}
