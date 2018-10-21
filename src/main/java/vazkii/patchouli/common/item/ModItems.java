package vazkii.patchouli.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.network.GuiHandler;

public class ModItems {

	public static Item book;
	
	public static void preInit() {
		book = new ItemModBook();
		
		MinecraftForge.EVENT_BUS.register(ModItems.class);
	}

	@SideOnly(Side.CLIENT)
	public static void setModels() {
		bindModel(book);
	}
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(book);
	}
	
	private static void bindModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
