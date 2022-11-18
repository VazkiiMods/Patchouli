package vazkii.patchouli.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

import java.util.function.BiConsumer;

public class PatchouliItems {

	public static final ResourceLocation BOOK_ID = new ResourceLocation(PatchouliAPI.MOD_ID, "guide_book");
	public static final Item BOOK = new PatchouliBookItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
		@Override
		public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> list) {
			BookRegistry.INSTANCE.books.values().stream().filter(book -> filter(book, creativeModeTab)).map(PatchouliBookItem::getItemStack).forEach(list::add);
		}
	};

	public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
		consumer.accept(BOOK_ID, BOOK);
	}

	public static void submitRecipeSerializerRegistrations(BiConsumer<ResourceLocation, RecipeSerializer<?>> consumer) {
		consumer.accept(new ResourceLocation(PatchouliAPI.MOD_ID, "shaped_book_recipe"), ShapedBookRecipe.SERIALIZER);
		consumer.accept(new ResourceLocation(PatchouliAPI.MOD_ID, "shapeless_book_recipe"), ShapelessBookRecipe.SERIALIZER);
	}

	private static boolean filter(Book book, CreativeModeTab creativeModeTab) {
		return !(book.noBook && book.isExtension) && (creativeModeTab == CreativeModeTab.TAB_SEARCH || book.creativeTab.equals(creativeModeTab.getRecipeFolderName()));
	}
}
