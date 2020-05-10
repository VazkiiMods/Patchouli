package vazkii.patchouli.common.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;

import java.util.List;

public class ItemModBook extends Item {

	private static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		super(new Item.Settings()
				.maxCount(1)
				.group(ItemGroup.MISC));

		addPropertyGetter(new Identifier("completion"), (stack, world, entity) -> {
			Book book = getBook(stack);
			float progression = 0F; // default incomplete

			if (book != null) {
				int totalEntries = 0;
				int unlockedEntries = 0;

				for (BookEntry entry : book.contents.entries.values()) {
					if (!entry.isSecret()) {
						totalEntries++;
						if (!entry.isLocked())
							unlockedEntries++;
					}
				}

				progression = ((float) unlockedEntries) / Math.max(1f, (float) totalEntries);
			}

			return progression;
		});
	}

	public static ItemStack forBook(Book book) {
		return forBook(book.id);
	}

	public static ItemStack forBook(Identifier book) {
		ItemStack stack = new ItemStack(PatchouliItems.book);

		CompoundTag cmp = new CompoundTag();
		cmp.putString(TAG_BOOK, book.toString());
		stack.setTag(cmp);

		return stack;
	}

	@Override
	public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
		String tabName = tab.getName();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension && (tab == ItemGroup.SEARCH || b.creativeTab.equals(tabName)))
				items.add(forBook(b));
		});
	}

	public static Book getBook(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains(TAG_BOOK))
			return null;

		String bookStr = stack.getTag().getString(TAG_BOOK);
		Identifier res = Identifier.tryParse(bookStr);
		return res == null ? null : BookRegistry.INSTANCE.books.get(res);
	}

	/* TODO fabric
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		Book book = getBook(itemStack);
		if(book != null)
			return book.owner.getModId();
	
		return super.getCreatorModId(itemStack);
	}
	*/

	@Override
	public Text getName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null)
			return new TranslatableText(book.name);

		return super.getName(stack);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		super.appendTooltip(stack, worldIn, tooltip, flagIn);

		Book book = getBook(stack);
		if (book != null && book.contents != null)
			tooltip.add(new LiteralText(book.contents.getSubtitle()).formatted(Formatting.GRAY));
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getStackInHand(handIn);
		Book book = getBook(stack);
		if (book == null)
			return new TypedActionResult<>(ActionResult.FAIL, stack);

		if (playerIn instanceof ServerPlayerEntity) {
			PatchouliAPI.instance.openBookGUI((ServerPlayerEntity) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, stack);
	}

}
