package vazkii.patchouli.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.function.Consumer;

public class ItemModBook extends Item {

	public ItemModBook(Properties properties) {
		super(properties.stacksTo(1));
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

		stack.set(PatchouliDataComponents.BOOK, book);

		return stack;
	}

	// SoftImplement IForgeItem
	public String getCreatorModId(ItemStack stack) {
		var book = getBook(stack);
		if (book != null) {
			return book.owner.getId();
		}
		return BuiltInRegistries.ITEM.getKey(this).getNamespace();
	}

	public static Book getBook(ItemStack stack) {
		ResourceLocation res = getBookId(stack);
		if (res == null) {
			return null;
		}
		return BookRegistry.INSTANCE.books.get(res);
	}

	private static ResourceLocation getBookId(ItemStack stack) {
		if (!stack.has(PatchouliDataComponents.BOOK)) {
			return null;
		}

		return stack.get(PatchouliDataComponents.BOOK);
	}

	@Override
	public Component getName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null) {
			return Component.translatable(book.name);
		}

		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
   

		ResourceLocation rl = getBookId(stack);
		if (flag.isAdvanced()) {
			tooltipAdder.accept(Component.literal("Book ID: " + rl).withStyle(ChatFormatting.GRAY));
		}

		Book book = getBook(stack);
		if (book != null && !book.getContents().isErrored()) {
			tooltipAdder.accept(book.getSubtitle().withStyle(ChatFormatting.GRAY));
		} else if (book == null) {
			if (rl == null) {
				tooltipAdder.accept(Component.translatable("item.patchouli.guide_book.undefined")
						.withStyle(ChatFormatting.DARK_GRAY));
			} else {
				tooltipAdder.accept(Component.translatable("item.patchouli.guide_book.invalid", rl)
						.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}


	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		Book book = getBook(stack);
		if (book == null) {
			return InteractionResult.FAIL;
		}

		if (playerIn instanceof ServerPlayer) {
			PatchouliAPI.get().openBookGUI((ServerPlayer) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return InteractionResult.SUCCESS;
	}

}