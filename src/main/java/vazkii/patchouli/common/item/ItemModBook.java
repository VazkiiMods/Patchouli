package vazkii.patchouli.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;

public class ItemModBook extends Item {
	
	private static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
		setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "guide_book"));
		setUnlocalizedName(getRegistryName().toString());
	}
	
	public static ItemStack forBook(Book book) {
		ItemStack stack = new ItemStack(ModItems.book);
		NBTTagCompound cmp = new NBTTagCompound();
		cmp.setString(TAG_BOOK, book.resourceLoc.toString());
		stack.setTagCompound(cmp);
		
		return stack;
	}
	
	@Override // TODO allow each book to be in its own tab
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(isInCreativeTab(tab))
			BookRegistry.INSTANCE.books.values().forEach(b -> items.add(forBook(b)));
	}
	
	public static Book getBook(ItemStack stack) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG_BOOK))
			return null;
		
		String bookStr = stack.getTagCompound().getString(TAG_BOOK);
		ResourceLocation res = new ResourceLocation(bookStr);
		return BookRegistry.INSTANCE.books.get(res);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Book book = getBook(stack);
		if(book != null)
			return I18n.translateToLocal(book.name).trim();
		
		return super.getItemStackDisplayName(stack);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		// TODO: sfx
//		if(!worldIn.isRemote)
//			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, AlquimiaSounds.book_open, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
	
		ItemStack stack = playerIn.getHeldItem(handIn);
		Book book = getBook(stack);
		if(book == null)
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		
		if(playerIn instanceof EntityPlayerMP)
			NetworkHandler.INSTANCE.sendTo(new MessageOpenBookGui(book.resourceLoc.toString()), (EntityPlayerMP) playerIn);
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
	
	
}
