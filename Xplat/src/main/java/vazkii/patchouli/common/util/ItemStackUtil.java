package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public final class ItemStackUtil {
	private static final Gson GSON = new GsonBuilder().create();

	private ItemStackUtil() {}

	public static String serializeStack(ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(BuiltInRegistries.ITEM.getKey(stack.getItem()));

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

	public static Triple<ResourceLocation, Integer, CompoundTag> parseItemStackString(String res) {
		String nbt = "";
		int nbtStart = res.indexOf("{");
		if (nbtStart > 0) {
			nbt = res.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			res = res.substring(0, nbtStart);
		}

		String[] upper = res.split("#");
		String count = "1";
		if (upper.length > 1) {
			res = upper[0];
			count = upper[1];
		}

		String[] tokens = res.split(":");
		if (tokens.length < 2) {
			throw new RuntimeException("Malformed item ID " + res);
		}

		ResourceLocation key = new ResourceLocation(tokens[0], tokens[1]);
		int countn = Integer.parseInt(count);
		CompoundTag tag = null;

		if (!nbt.isEmpty()) {
			try {
				tag = TagParser.parseTag(nbt);
			} catch (CommandSyntaxException e) {
				throw new RuntimeException("Failed to parse ItemStack JSON", e);
			}
		}

		return ImmutableTriple.of(key, countn, tag);
	}

	public static ItemStack loadFromParsed(Triple<ResourceLocation, Integer, CompoundTag> parsed) {
		var key = parsed.getLeft();
		var count = parsed.getMiddle();
		var nbt = parsed.getRight();
		Optional<Item> maybeItem = BuiltInRegistries.ITEM.getOptional(key);
		if (maybeItem.isEmpty()) {
			throw new RuntimeException("Unknown item ID: " + key);
		}
		Item item = maybeItem.get();
		ItemStack stack = new ItemStack(item, count);

		if (nbt != null) {
			stack.setTag(nbt);
		}
		return stack;
	}

	public static ItemStack loadStackFromString(String res) {
		return loadFromParsed(parseItemStackString(res));
	}

	public static String serializeIngredient(Ingredient ingredient) {
		ItemStack[] stacks = ingredient.getItems();
		String[] stacksSerialized = new String[stacks.length];
		for (int i = 0; i < stacks.length; i++) {
			stacksSerialized[i] = serializeStack(stacks[i]);
		}

		return String.join(",", stacksSerialized);
	}

	public static Ingredient loadIngredientFromString(String ingredientString) {
		return Ingredient.of(loadStackListFromString(ingredientString).toArray(new ItemStack[0]));
	}

	public static String serializeStackList(List<ItemStack> stacks) {
		StringJoiner joiner = new StringJoiner(",");
		for (ItemStack stack : stacks) {
			joiner.add(serializeStack(stack));
		}
		return joiner.toString();
	}

	public static List<ItemStack> loadStackListFromString(String ingredientString) {
		String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
		List<ItemStack> stacks = new ArrayList<>();
		for (String s : stacksSerialized) {
			if (s.startsWith("tag:")) {
				var key = TagKey.create(Registries.ITEM, new ResourceLocation(s.substring(4)));
				BuiltInRegistries.ITEM.getTag(key).ifPresent(tag -> tag.stream().forEach(item -> stacks.add(new ItemStack(item))));
			} else {
				stacks.add(loadStackFromString(s));
			}
		}
		return stacks;
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
			if (b.getBookItem().sameItem(stack)) {
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
			return obj == this || (obj instanceof StackWrapper && ItemStack.isSame(stack, ((StackWrapper) obj).stack));
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

	private static String[] splitStacksFromSerializedIngredient(String ingredientSerialized) {
		final List<String> result = new ArrayList<>();

		int lastIndex = 0;
		int braces = 0;
		Character insideString = null;
		for (int i = 0; i < ingredientSerialized.length(); i++) {
			switch (ingredientSerialized.charAt(i)) {
			case '{':
				if (insideString == null) {
					braces++;
				}
				break;
			case '}':
				if (insideString == null) {
					braces--;
				}
				break;
			case '\'':
				insideString = insideString == null ? '\'' : null;
				break;
			case '"':
				insideString = insideString == null ? '"' : null;
				break;
			case ',':
				if (braces <= 0) {
					result.add(ingredientSerialized.substring(lastIndex, i));
					lastIndex = i + 1;
					break;
				}
			}
		}

		result.add(ingredientSerialized.substring(lastIndex));

		return result.toArray(new String[0]);
	}

	public static ItemStack loadStackFromJson(JsonObject json) {
		// Adapted from net.minecraftforge.common.crafting.CraftingHelper::getItemStack
		String itemName = json.get("item").getAsString();

		Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(itemName)).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemName + "'")
		);

		ItemStack stack = new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));

		if (json.has("nbt")) {
			try {
				JsonElement element = json.get("nbt");
				CompoundTag nbt;
				if (element.isJsonObject()) {
					nbt = TagParser.parseTag(GSON.toJson(element));
				} else {
					nbt = TagParser.parseTag(element.getAsString());
				}
				stack.setTag(nbt);
			} catch (CommandSyntaxException e) {
				throw new IllegalArgumentException("Invalid NBT Entry: " + e, e);
			}
		}

		return stack;
	}
}
