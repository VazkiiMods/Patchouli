package vazkii.patchouli.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.Patchouli;

public class ItemModBook extends Item {

	public ItemModBook() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
		setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "guide_book"));
		setUnlocalizedName(getRegistryName().toString());
	}
	
}
