package vazkii.patchouli.common.network.message;

import net.minecraftforge.fml.network.NetworkEvent.Context;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.network.IMessage;

public class MessageOpenBookGui implements IMessage {

	private static final long serialVersionUID = -8413856876282832583L;
	
	public String book;
	
	public MessageOpenBookGui() { }
	
	public MessageOpenBookGui(String book) { 
		this.book = book;
	}
	
	@Override
	public boolean receive(Context context) {
		context.enqueueWork(() -> {
			ClientBookRegistry.INSTANCE.displayBookGui(book);
		});
		
		return true;
	}

}
