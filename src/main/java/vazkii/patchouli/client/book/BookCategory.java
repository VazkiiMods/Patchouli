package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

public class BookCategory extends AbstractReadStateHolder implements Comparable<BookCategory> {

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
		return !PatchouliConfig.disableAdvancementLocking.get() && locked;
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
		if(!PatchouliConfig.disableAdvancementLocking.get() && o.locked != this.locked)
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
	
	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> entryStream = entries.stream().filter(e -> !e.isLocked()).map(BookEntry::getReadState);
		Stream<EntryDisplayState> childrenStream = children.stream().map(BookCategory::getReadState);
		return mostImportantState(entryStream, childrenStream);
	}
	
	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();
		
		if(parentCategory != null)
			parentCategory.markReadStateDirty();
		else book.contents.markReadStateDirty();
	}

}
