package vazkii.patchouli.common.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;

import javax.annotation.Nullable;

public class ItemModBook extends Item {

	private static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		setMaxStackSize(1);
		setCreativeTab(ItemGroup.MISC);
		setRegistryName(new ResourceLocation(Patchouli.MOD_ID, "guide_book"));
		setTranslationKey(getRegistryName().toString());

		this.addPropertyOverride(new ResourceLocation("completion"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
				Book book = getBook(stack);
				float progression = 0f; // default incomplete

				if (book != null) {
					int totalEntries = 0;
					int unlockedEntries = 0;

					for(BookEntry entry : book.contents.entries.values()) {
						if (!entry.isSecret()) {
							totalEntries++;
							if(!entry.isLocked())
								unlockedEntries++;
						}
					}

					progression = ((float) unlockedEntries) / Math.max(1f, (float) totalEntries);
				}

				return progression;
			}
		});
	}

	public static ItemStack forBook(Book book) {
		return forBook(book.resourceLoc.toString());
	}
	
	public static ItemStack forBook(String book) {
		ItemStack stack = new ItemStack(PatchouliItems.book);

		CompoundNBT cmp = new CompoundNBT();
		cmp.setString(TAG_BOOK, book);
		stack.setTagCompound(cmp);

		return stack;
	}

	@Override 
	@OnlyIn(Dist.CLIENT)
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
		String tabName = tab.getTabLabel();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if(!b.noBook && !b.isExtension && (tab == ItemGroup.SEARCH || b.creativeTab.equals(tabName)))
				items.add(forBook(b));
		});
	}

	public static Book getBook(ItemStack stack) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG_BOOK))
			return null;

		String bookStr = stack.getTagCompound().getString(TAG_BOOK);
		ResourceLocation res = new ResourceLocation(bookStr);
		return BookRegistry.INSTANCE.books.get(res);
	}

	@Override
	public String getCreatorModId(ItemStack itemStack) {
		Book book = getBook(itemStack);
		if(book != null)
			return book.owner.getModId();

		return super.getCreatorModId(itemStack);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Book book = getBook(stack);
		if(book != null)
			return I18n.translateToLocal(book.name).trim();

		return super.getItemStackDisplayName(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		Book book = getBook(stack);
		if(book != null && book.contents != null)
			tooltip.add(book.contents.getSubtitle());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		Book book = getBook(stack);
		if(book == null)
			return new ActionResult<>(ActionResultType.FAIL, stack);

		if(playerIn instanceof ServerPlayerEntity) {
			NetworkHandler.INSTANCE.sendTo(new MessageOpenBookGui(book.resourceLoc.toString()), (ServerPlayerEntity) playerIn);
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open); 
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, sfx, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}


}
