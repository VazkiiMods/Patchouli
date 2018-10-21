package vazkii.patchouli.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiHandler implements IGuiHandler {

	public static final int BOOK_GUI = 0;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		case BOOK_GUI: 
			return GuiBook.getCurrentGui();
		}
		
		return null;
	}

}
