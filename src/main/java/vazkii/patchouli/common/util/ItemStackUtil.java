package vazkii.patchouli.common.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackUtil {
	
	public static String serializeStack(ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(stack.getItem().getRegistryName().toString());
		builder.append(":");
		builder.append(stack.getDamage());
		
		int count = stack.getCount();
		if (count > 1) {
			builder.append("#");
			builder.append(count);
		}
		
		if(stack.hasTag())
			builder.append(stack.getTag().toString());
		
		return builder.toString();
	}

	public static ItemStack loadStackFromString(String res) {
		String nbt = "";
		int nbtStart = res.indexOf("{");
		if(nbtStart > 0) {
			nbt = res.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			res = res.substring(0, nbtStart);
		}
		
		int meta = 0;
		
		String[] upper = res.split("#");
		String count = "1";
		if(upper.length > 1) {
			res = upper[0];
			count = upper[1];
		}
		
		String[] tokens = res.split(":");
		if(tokens.length < 2)
			return ItemStack.EMPTY;
		
		if(tokens.length == 3)
			meta = Integer.parseInt(tokens[2]);
		
		int countn = Integer.parseInt(count);
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tokens[0], tokens[1]));
		ItemStack stack = new ItemStack(item, countn);
		if(meta != 0)
			stack.setDamage(meta);
		
		if(!nbt.isEmpty())
			try {
				stack.setTag(JsonToNBT.getTagFromJson(nbt));
			} catch (CommandSyntaxException e) {
				throw new RuntimeException("Failed to parse ItemStack JSON" , e);
			}
		
		return stack;
	}
	
	public static String serializeIngredient(Ingredient ingredient) {
		ItemStack[] stacks = ingredient.getMatchingStacks();
		String[] stacksSerialized = new String[stacks.length];
		for (int i = 0; i < stacks.length; i++) {
			stacksSerialized[i] = serializeStack(stacks[i]);
		}

		return String.join(",", stacksSerialized);
	}
	
	public static Ingredient loadIngredientFromString(String ingredientString) {
		String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
		List<ItemStack> stacks = Lists.newArrayList();
		for (int i = 0; i < stacksSerialized.length; i++) { // TODO allow for tags
//			if (stacksSerialized[i].startsWith("ore:")) {
//				OreIngredient ore = new OreIngredient(stacksSerialized[i].substring(4));
//				for (ItemStack stack : ore.getMatchingStacks())
//					stacks.add(stack);
//			}
			
			stacks.add(loadStackFromString(stacksSerialized[i]));
		}

		return Ingredient.fromStacks(stacks.toArray(new ItemStack[stacks.size()]));
	}
	
	public static StackWrapper wrapStack(ItemStack stack) {
		return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
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
			return stack.getItem().hashCode() ^ stack.getDamage() * 31;
		}
		
		@Override
		public String toString() {
			return "Wrapper[" + stack.toString() + "]";
		}
		
	}

	private static String[] splitStacksFromSerializedIngredient (String ingredientSerialized) {
		final List<String> result = new ArrayList<>();

		int lastIndex = 0;
		int braces = 0;
		Character insideString = null;
		for (int i = 0; i < ingredientSerialized.length(); i++) {
			switch (ingredientSerialized.charAt(i)) {
				case '{':
					if (insideString == null) braces++;
					break;
				case '}':
					if (insideString == null)
						braces--;
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

		return result.toArray(new String[result.size()]);
	}
	
}
