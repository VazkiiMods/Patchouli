package vazkii.patchouli.common.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	@OnlyIn(Dist.CLIENT)
	public IMessage handleMessage(MessageContext context) {
		ClientTicker.addAction(() -> ClientBookRegistry.INSTANCE.displayBookGui(book));
		
		return null;
	}

}
