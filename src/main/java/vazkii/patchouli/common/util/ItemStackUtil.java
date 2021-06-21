package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nullable;

import java.util.Collection;

public final class ItemStackUtil {
	private static final Gson GSON = new GsonBuilder().create();

	private ItemStackUtil() {}

	public static String serializeStack(ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(Registry.ITEM.getId(stack.getItem()).toString());

		int count = stack.getCount();
		if (count > 1) {
			builder.append("#");
			builder.append(count);
		}

		if (stack.hasTag()) {
			Dynamic<?> dyn = new Dynamic<>(NbtOps.INSTANCE, stack.getTag());
			JsonElement j = dyn.convert(JsonOps.INSTANCE).getValue();
			builder.append(GSON.toJson(j));
		}

		return builder.toString();
	}

	public static StackWrapper wrapStack(ItemStack stack) {
		return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
	}

	@Nullable
	public static Book getBookFromStack(ItemStack stack) {
		if (stack.getItem() instanceof ItemModBook) {
			return ItemModBook.getBook(stack);
		}

		Collection<Book> books = BookRegistry.INSTANCE.books.values();
		for (Book b : books) {
			if (b.getBookItem().isItemEqual(stack)) {
				return b;
			}
		}

		return null;
	}

	public static class StackWrapper {

		public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);

		public final ItemStack stack;

		public StackWrapper(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof StackWrapper && ItemStack.areItemsEqual(stack, ((StackWrapper) obj).stack));
		}

		@Override
		public int hashCode() {
			return stack.getItem().hashCode();
		}

		@Override
		public String toString() {
			return "Wrapper[" + stack.toString() + "]";
		}

	}

	public static ItemStack loadStackFromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return ItemStack.EMPTY;
		} else if (json.isJsonPrimitive()) {
			Identifier id = new Identifier(json.getAsString());
			Item item = Registry.ITEM.getOrEmpty(id).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + id + "'"));
			return new ItemStack(item);
		} else {
			return ShapedRecipe.outputFromJson(json.getAsJsonObject());
		}
	}
}
