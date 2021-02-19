package vazkii.patchouli.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.List;

public class ItemModBook extends Item {

	public static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		super(new Item.Properties()
				.maxStackSize(1)
				.group(ItemGroup.MISC));
	}

	public static float getCompletion(ItemStack stack) {
		Book book = getBook(stack);
		float progression = 0F; // default incomplete

		if (book != null) {
			int totalEntries = 0;
			int unlockedEntries = 0;

			for (BookEntry entry : book.contents.entries.values()) {
				if (!entry.isSecret()) {
					totalEntries++;
					if (!entry.isLocked()) {
						unlockedEntries++;
					}
				}
			}

			progression = ((float) unlockedEntries) / Math.max(1f, (float) totalEntries);
		}

		return progression;
	}

	public static ItemStack forBook(Book book) {
		return forBook(book.id);
	}

	public static ItemStack forBook(ResourceLocation book) {
		ItemStack stack = new ItemStack(PatchouliItems.book);

		CompoundNBT cmp = new CompoundNBT();
		cmp.putString(TAG_BOOK, book.toString());
		stack.setTag(cmp);

		return stack;
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
		String tabName = ObfuscationReflectionHelper.getPrivateValue(ItemGroup.class, tab, "field_78034_o");
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension && (tab == ItemGroup.SEARCH || b.creativeTab.equals(tabName))) {
				items.add(forBook(b));
			}
		});
	}

	public static Book getBook(ItemStack stack) {
		ResourceLocation res = getBookId(stack);
		if (res == null) {
			return null;
		}
		return BookRegistry.INSTANCE.books.get(res);
	}

	private static ResourceLocation getBookId(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains(TAG_BOOK)) {
			return null;
		}

		String bookStr = stack.getTag().getString(TAG_BOOK);
		return ResourceLocation.tryCreate(bookStr);
	}

	@Override
	public String getCreatorModId(ItemStack itemStack) {
		Book book = getBook(itemStack);
		if (book != null) {
			return book.owner.getModId();
		}

		return super.getCreatorModId(itemStack);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null) {
			return new TranslationTextComponent(book.name);
		}

		return super.getDisplayName(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		Book book = getBook(stack);
		if (book != null && book.contents != null) {
			tooltip.add(book.getSubtitle().mergeStyle(TextFormatting.GRAY));
		} else if (book == null) {
			ResourceLocation rl = getBookId(stack);
			if (rl == null) {
				tooltip.add(new TranslationTextComponent("item.patchouli.guide_book.undefined")
						.mergeStyle(TextFormatting.DARK_GRAY));
			} else {
				tooltip.add(new TranslationTextComponent("item.patchouli.guide_book.invalid", rl)
						.mergeStyle(TextFormatting.DARK_GRAY));
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		Book book = getBook(stack);
		if (book == null) {
			return new ActionResult<>(ActionResultType.FAIL, stack);
		}

		if (playerIn instanceof ServerPlayerEntity) {
			PatchouliAPI.get().openBookGUI((ServerPlayerEntity) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

}
