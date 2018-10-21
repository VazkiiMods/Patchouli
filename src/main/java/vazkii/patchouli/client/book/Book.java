package vazkii.patchouli.client.book;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

public class Book {

	public final Map<ResourceLocation, BookCategory> categories = new HashMap();
	public final Map<ResourceLocation, BookEntry> entries = new HashMap();
	public final Map<StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap();
	
	public Pair<BookEntry, Integer> getEntryForStack(ItemStack stack) {
		return recipeMappings.get(ItemStackUtil.wrapStack(stack));
	}
	
	public void reloadRegistry() {
		categories.clear();
		entries.clear();
		recipeMappings.clear();
	}
	
	public void loadContents() {
		
	}

	
}
