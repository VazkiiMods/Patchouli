package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData;
import vazkii.patchouli.client.book.page.PageEmpty;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

public class BookEntry implements Comparable<BookEntry> {

	String name, category, flag;

	@SerializedName("icon")
	String iconRaw;

	boolean priority = false;
	boolean secret = false;
	@SerializedName("read_by_default")
	boolean readByDefault = false;
	BookPage[] pages;
	String advancement;
	int sortnum;

	transient ResourceLocation resource;
	transient Book book, trueProvider;
	transient BookCategory lcategory = null;
	transient BookIcon icon = null;
	transient List<BookPage> realPages = new ArrayList<>();
	transient List<StackWrapper> relevantStacks = new LinkedList<>();
	transient boolean locked;

	transient boolean built;

	public String getName() {
		return name;
	}

	public List<BookPage> getPages() {
		List<BookPage> pages = PatchouliConfig.disableAdvancementLocking ? realPages : realPages.stream().filter(BookPage::isPageUnlocked).collect(Collectors.toList());

		return pages.isEmpty() ? NO_PAGE : pages;
	}

	public int getPageFromAnchor(String anchor) {
		List<BookPage> pages = getPages();
		for (int i = 0; i < pages.size(); i++) {
			BookPage page = pages.get(i);
			if (page.anchor != null && anchor.equals(page.anchor))
				return i;
		}

		return -1;
	}

	private static final List<BookPage> NO_PAGE = ImmutableList.of(new PageEmpty());

	public boolean isPriority() {
		return priority;
	}

	public BookIcon getIcon() {
		if(icon == null)
			icon = new BookIcon(iconRaw); 

		return icon;
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
		boolean currLocked = locked;
		locked = advancement != null && !advancement.isEmpty() && !ClientAdvancements.hasDone(advancement);

		if(!locked && currLocked != locked)
			book.markUpdated();
	}

	public boolean isLocked() {
		if(isSecret())
			return locked;
		return !PatchouliConfig.disableAdvancementLocking && locked;
	}

	public boolean isUnread() {
		BookData data = PersistentData.data.getBookData(book);
		return data != null && getResource() != null && !readByDefault && !isLocked() && !data.viewedEntries.contains(getResource().toString());
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

	public boolean isFoundByQuery(String query) {
		if(getName().toLowerCase().contains(query))
			return true;
		
		for(StackWrapper wrapper : relevantStacks)
			if(StringUtils.stripControlCodes(wrapper.stack.getDisplayName()).toLowerCase().contains(query))
				return true;
		
		return false;
	}

	@Override
	public int compareTo(BookEntry o) {
		if(o.locked != this.locked)
			return this.locked ? 1 : -1;

		if(o.priority != this.priority)
			return this.priority ? -1 : 1;

		int sort = this.sortnum - o.sortnum;

		return sort == 0 ? this.name.compareTo(o.name) : sort;
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
		for(int i = 0; i < pages.length; i++)
			if(pages[i].canAdd(book)) {
				realPages.add(pages[i]);
				try {
					pages[i].build(this, i);
				} catch(Exception e) {
					throw new RuntimeException("Error while loading entry " + resource + " page " + i, e);
				}
			}

		built = true;
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

	public Book getBook() {
		return book;
	}

	public Book getTrueProvider() {
		return trueProvider;
	}

	public boolean isExtension() {
		return getTrueProvider() != null && getTrueProvider() != getBook();
	}

}
