package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookCategory implements Comparable<BookCategory> {

	String name, description, icon, parent, flag;
	int sortnum;
	
	transient Book book;
	transient boolean checkedParent = false;
	transient BookCategory parentCategory;
	transient List<BookCategory> children = new ArrayList<>();
	transient List<BookEntry> entries = new ArrayList<>();
	transient boolean locked;
	transient ItemStack iconItem = null;
	transient ResourceLocation resource;
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ItemStack getIconItem() {
		if(iconItem == null)
			iconItem = ItemStackUtil.loadStackFromString(icon);
		
		return iconItem;
	}
	
	public void addEntry(BookEntry entry) {
		this.entries.add(entry);
	}
	
	public void addChildCategory(BookCategory category) {
		children.add(category);
	}
	
	public List<BookEntry> getEntries() {
		return entries;
	}
	
	public BookCategory getParentCategory() {
		if(!checkedParent && !isRootCategory()) {
			parentCategory = book.contents.categories.get(new ResourceLocation(parent));
			checkedParent = true;
		}
		
		return parentCategory;
	}
	
	public void updateLockStatus(boolean rootOnly) {
		if(rootOnly && !isRootCategory())
			return;

		children.forEach((c) ->  c.updateLockStatus(false));
		
		locked = true;
		for(BookCategory c : children)
			if(!c.isLocked()) {
				locked = false;
				return;
			}
		
		for(BookEntry e : entries) {
			if(!e.isLocked()) {
				locked = false;
				return;
			}
		}
	}
	
	public boolean isLocked() {
		return !PatchouliConfig.disableAdvancementLocking && locked;
	}
	
	public boolean isUnread() {
		for(BookEntry e : entries)
			if(e.isUnread())
				return true;
		
		for(BookCategory c : children)
			if(c.isUnread())
				return true;
		
		return false;
	}
	
	public boolean isRootCategory() {
		return parent == null || parent.isEmpty();
	}
	
	public ResourceLocation getResource() {
		return resource;
	}
	
	public boolean canAdd() {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}
	
	@Override
	public int compareTo(BookCategory o) {
		if(o.locked != this.locked)
			return this.locked ? 1 : -1;
		
		return this.sortnum - o.sortnum;
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public void build(ResourceLocation resource) {
		this.resource = resource;
		BookCategory parent = getParentCategory();
		if(parent != null)
			parent.addChildCategory(this);
	}
	
	
	public Book getBook() {
		return book;
	}
	
}
