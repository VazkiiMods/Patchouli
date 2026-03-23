package vazkii.patchouli.common.util;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.xplat.IXplatAbstractions;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ItemStackUtil {
	private ItemStackUtil() {}

	public static ItemStackTemplate deserializeStack(String string, HolderLookup.Provider registries) {
		StringReader reader = new StringReader(string.trim());
		ItemParser itemParser = new ItemParser(registries);
		try {
			ItemInput result = itemParser.parse(reader);
			int count = 1;
			if (reader.canRead()) {
				reader.expect('#');
				count = reader.readInt();
			}
			return new ItemStackTemplate(result.item(), count, result.components());
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static ItemStack loadStackFromString(String res, HolderLookup.Provider registries) {
		return deserializeStack(res, registries).create();
	}

	public static Ingredient loadIngredientFromString(String ingredientString, HolderLookup.Provider registries) {
		List<Either<ItemStack, HolderSet<Item>>> stacksOrTags = loadStackListFromString(ingredientString, registries);
		List<Ingredient> ingredients = new ArrayList<>();
		for (Either<ItemStack, HolderSet<Item>> stackOrTag : stacksOrTags) {
			ingredients.add(stackOrTag.map(IXplatAbstractions.INSTANCE::createComponentIngredient, Ingredient::of));
		}
		return switch (ingredients.size()) {
		case 0 -> throw new UnsupportedOperationException("Ingredients can't be empty");
		case 1 -> ingredients.getFirst();
		default -> IXplatAbstractions.INSTANCE.createCompoundIngredient(ingredients.toArray(new Ingredient[0]));
		};
	}

	public static List<Either<ItemStack, HolderSet<Item>>> loadStackListFromString(String ingredientString, HolderLookup.Provider registries) {
		String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
		List<Either<ItemStack, HolderSet<Item>>> stacks = new ArrayList<>();
		for (String s : stacksSerialized) {
			if (s.isEmpty())
				continue;
			if (s.startsWith("tag:")) {
				var key = TagKey.create(Registries.ITEM, Identifier.parse(s.substring(4)));
				registries.lookupOrThrow(Registries.ITEM).get(key).ifPresent(holders -> stacks.add(Either.right(holders)));
			} else {
				stacks.add(Either.left(loadStackFromString(s, registries)));
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
			if (ItemStack.isSameItem(b.getBookItem(), stack)) {
				return b;
			}
		}

		return null;
	}

	public static List<ItemStack> getStacksFromIngredient(Ingredient ingredient, Level level) {
		return ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(level));
	}

	public static class StackWrapper {

		public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);

		public final ItemStack stack;

		public StackWrapper(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof StackWrapper && ItemStack.isSameItem(stack, ((StackWrapper) obj).stack));
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
		int brackets = 0;
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
			case '[':
				if (insideString == null) {
					brackets++;
				}
				break;
			case ']':
				if (insideString == null) {
					brackets--;
				}
				break;
			case '\'':
				insideString = insideString == null ? '\'' : null;
				break;
			case '"':
				insideString = insideString == null ? '"' : null;
				break;
			case ',':
				if (braces <= 0 && brackets <= 0) {
					result.add(ingredientSerialized.substring(lastIndex, i));
					lastIndex = i + 1;
					break;
				}
			}
		}

		result.add(ingredientSerialized.substring(lastIndex));

		return result.toArray(new String[0]);
	}

	public static ItemStack loadStackFromJson(JsonObject json, HolderLookup.Provider registries) {
		String itemName = json.get("item").getAsString();

		Item item = BuiltInRegistries.ITEM.getOptional(Identifier.tryParse(itemName)).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemName + "'")
		);

		ItemStack stack = new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));

		if (json.has("components")) {
			DataComponentMap.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), json.get("components")).result()
					.ifPresent(stack::applyComponents);
		}

		return stack;
	}
}
