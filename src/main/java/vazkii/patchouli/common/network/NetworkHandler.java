/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [11/01/2016, 21:58:25 (GMT)]
 */
package vazkii.patchouli.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.network.message.MessageSyncAdvancements;

// Basically a copy of the ARL one but I want to avoid the dep so hey it's here too
public class NetworkHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Patchouli.MOD_ID);

	private static int i = 0;

	public static void registerMessages() {
		register(MessageSyncAdvancements.class, Side.CLIENT);
		register(MessageOpenBookGui.class, Side.CLIENT);

		NetworkMessage.mapHandler(String[].class, NetworkHandler::readStringArray, NetworkHandler::writeStringArray);
	}
	
	public static String[] readStringArray(ByteBuf buf) {
		int len = buf.readInt();
		String[] strs = new String[len];
		for(int i = 0; i < len; i++)
			strs[i] = ByteBufUtils.readUTF8String(buf);
		
		return strs;
	}
	
	public static void writeStringArray(String[] arr, ByteBuf buf) {
		buf.writeInt(arr.length);
		for(int i = 0; i < arr.length; i++)
			ByteBufUtils.writeUTF8String(buf, arr[i]);
	}
	
	public static void register(Class clazz, Side handlerSide) {
		INSTANCE.registerMessage(clazz, clazz, i++, handlerSide);
	}
	

}
