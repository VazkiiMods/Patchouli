package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookCategory implements Comparable<BookCategory> {

	String name, description, parent, flag;
	@SerializedName("icon")
	String iconRaw;
	int sortnum;
	boolean secret = false;

	transient Book book, trueProvider;
	transient boolean checkedParent = false;
	transient BookCategory parentCategory;
	transient List<BookCategory> children = new ArrayList<>();
	transient List<BookEntry> entries = new ArrayList<>();
	transient boolean locked;
	transient BookIcon icon = null;
	transient ResourceLocation resource;
	
	transient boolean built;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public BookIcon getIcon() {
		if(icon == null)
			icon = new BookIcon(iconRaw);

		return icon;
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
			if(parent.contains(":"))
				parentCategory = book.contents.categories.get(new ResourceLocation(parent));
			else parentCategory = book.contents.categories.get(new ResourceLocation(book.getModNamespace(), parent));

			checkedParent = true;
		}

		return parentCategory;
	}

	public void updateLockStatus(boolean rootOnly) {
		if(rootOnly && !isRootCategory())
			return;

		children.forEach((c) ->  c.updateLockStatus(false));

		boolean currLocked = locked;

		updateLocked: {
			locked = true;
			for(BookCategory c : children)
				if(!c.isLocked()) {
					locked = false;
					break updateLocked;
				}

			for(BookEntry e : entries) {
				if(!e.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}
		}

		if(!locked && currLocked != locked)
			book.markUpdated();
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
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
		if(book.isExtension) {
			this.book = book.extensionTarget;
			trueProvider = book;
		} else this.book = book;	
	}

	public void build(ResourceLocation resource) {
		if(built)
			return;
		
		this.resource = resource;
		BookCategory parent = getParentCategory();
		if(parent != null)
			parent.addChildCategory(this);
		
		built = true;
	}

	public Book getBook() {
		return book;
	}
	
	public Book getTrueProvider() {
		return trueProvider;
	}

	public boolean isExtension() {
		return getTrueProvider() != getBook();
	}

}
