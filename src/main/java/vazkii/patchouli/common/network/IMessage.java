package vazkii.patchouli.common.network;

import java.io.Serializable;

import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessage extends Serializable {

	public boolean receive(NetworkEvent.Context context);
	
}
