package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

public class BookEntry implements Comparable<BookEntry> {

	String name, icon, category, flag;
	boolean priority = false;
	boolean secret = false;
	@SerializedName("read_by_default")
	boolean readByDefault = false;
	BookPage[] pages;
	String advancement;
	
	transient ResourceLocation resource;
	transient Book book = null;
	transient BookCategory lcategory = null;
	transient ItemStack iconItem = null;
	transient List<BookPage> realPages = new ArrayList();
	transient List<StackWrapper> relevantStacks = new LinkedList();
	transient boolean locked;
	
	public String getName() {
		return name;
	}

	public List<BookPage> getPages() {
		return realPages;
	}
	
	public boolean isPriority() {
		return priority;
	}
	
	public ItemStack getIconItem() {
		if(iconItem == null)
			iconItem = ItemStackUtil.loadStackFromString(icon);
		
		return iconItem;
	}
	
	public BookCategory getCategory() {
		if(lcategory == null) {
			if(category.contains(":"))
				lcategory = book.contents.categories.get(new ResourceLocation(category));
			else lcategory = book.contents.categories.get(new ResourceLocation(book.getModNamespace(), category));
		}
		
		return lcategory;
	}
	
	public void updateLockStatus() {
		locked = advancement != null && !advancement.isEmpty() && !ClientAdvancements.hasDone(advancement);
	}
	
	public boolean isLocked() {
		if(isSecret())
			return locked;
		return !PatchouliConfig.disableAdvancementLocking && locked;
	}
	
	public boolean isUnread() {
		BookData data = PersistentData.data.getBookData(book);
		return !readByDefault && !isLocked() && !data.viewedEntries.contains(getResource().toString());
	}
	
	public boolean isSecret() {
		return secret;
	}
	
	public boolean shouldHide() {
		return isSecret() && isLocked();
	}
	
	public ResourceLocation getResource() {
		return resource;
	}
	
	public boolean canAdd() {
		return (flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag)) && getCategory() != null;
	}
	
	@Override
	public int compareTo(BookEntry o) {
		if(o.locked != this.locked)
			return this.locked ? 1 : -1;

		if(o.priority != this.priority)
			return this.priority ? -1 : 1;
		
		return this.name.compareTo(o.name);
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public void build(ResourceLocation resource) {
		this.resource = resource;
		for(int i = 0; i < pages.length; i++)
			if(pages[i].canAdd()) {
				realPages.add(pages[i]);
				pages[i].build(this, i);
			}
	}
	
	public void addRelevantStack(ItemStack stack, int page) {
		StackWrapper wrapper = ItemStackUtil.wrapStack(stack);
		relevantStacks.add(wrapper);
		
		if(!book.contents.recipeMappings.containsKey(wrapper))
			book.contents.recipeMappings.put(wrapper, Pair.of(this, page / 2));
	}
	
	public boolean isStackRelevant(ItemStack stack) {
		return relevantStacks.contains(ItemStackUtil.wrapStack(stack));
	}
	
}
