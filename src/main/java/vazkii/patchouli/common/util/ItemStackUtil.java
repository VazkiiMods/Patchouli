package vazkii.patchouli.common.util;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemStackUtil {

	public static ItemStack loadStackFromString(String res) {
		int meta = 0;
		String[] tokens = res.split(":");
		if(tokens.length < 2)
			return ItemStack.EMPTY;
		
		if(tokens.length == 3)
			meta = Integer.parseInt(tokens[2]);
		
		Item item = Item.REGISTRY.getObject(new ResourceLocation(tokens[0], tokens[1]));
		return new ItemStack(item, 1, meta);
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
			return stack.getItem().hashCode() ^ stack.getItemDamage() * 31;
		}
		
		@Override
		public String toString() {
			return "Wrapper[" + stack.toString() + "]";
		}
		
	}
	
}
