package vazkii.patchouli.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
				.stacksTo(1)
				.tab(CreativeModeTab.TAB_MISC));
	}

	public static float getCompletion(ItemStack stack) {
		Book book = getBook(stack);
		float progression = 0F; // default incomplete

		if (book != null) {
			int totalEntries = 0;
			int unlockedEntries = 0;

			for (BookEntry entry : book.getContents().entries.values()) {
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
		ItemStack stack = new ItemStack(PatchouliItems.BOOK);

		CompoundTag cmp = new CompoundTag();
		cmp.putString(TAG_BOOK, book.toString());
		stack.setTag(cmp);

		return stack;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		String tabName = tab.getRecipeFolderName();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension && (tab == CreativeModeTab.TAB_SEARCH || b.creativeTab.equals(tabName))) {
				items.add(forBook(b));
			}
		});
	}

	// SoftImplement IForgeItem
	public String getCreatorModId(ItemStack stack) {
		var book = getBook(stack);
		if (book != null) {
			return book.owner.getId();
		}
		return Registry.ITEM.getKey(this).getNamespace();
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
		return ResourceLocation.tryParse(bookStr);
	}

	/* TODO fabric
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		Book book = getBook(itemStack);
		if (book != null) {
			return book.owner.getModId();
		}
	
		return super.getCreatorModId(itemStack);
	}
	*/

	@Override
	public Component getName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null) {
			return new TranslatableComponent(book.name);
		}

		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		ResourceLocation rl = getBookId(stack);
		if (flagIn.isAdvanced()) {
			tooltip.add(new TextComponent("Book ID: " + rl).withStyle(ChatFormatting.GRAY));
		}

		Book book = getBook(stack);
		if (book != null && !book.getContents().isErrored()) {
			tooltip.add(book.getSubtitle().withStyle(ChatFormatting.GRAY));
		} else if (book == null) {
			if (rl == null) {
				tooltip.add(new TranslatableComponent("item.patchouli.guide_book.undefined")
						.withStyle(ChatFormatting.DARK_GRAY));
			} else {
				tooltip.add(new TranslatableComponent("item.patchouli.guide_book.invalid", rl)
						.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		Book book = getBook(stack);
		if (book == null) {
			return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
		}

		if (playerIn instanceof ServerPlayer) {
			PatchouliAPI.get().openBookGUI((ServerPlayer) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

}
