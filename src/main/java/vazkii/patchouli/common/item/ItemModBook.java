package vazkii.patchouli.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.network.GuiHandler;

public class ItemModBook extends Item {

	public ItemModBook() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
		setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "guide_book"));
		setUnlocalizedName(getRegistryName().toString());
	}
	
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		// TODO: sfx
//		if(!worldIn.isRemote)
//			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, AlquimiaSounds.book_open, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
		
		playerIn.openGui(Patchouli.instance, GuiHandler.BOOK_GUI, worldIn, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
	
}
