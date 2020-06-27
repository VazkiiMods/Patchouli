package vazkii.patchouli.client.book;

import com.google.common.collect.Streams;
import com.google.gson.annotations.SerializedName;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BookCategory extends AbstractReadStateHolder implements Comparable<BookCategory> {

	private String name, description, parent, flag;
	@SerializedName("icon") private String iconRaw;
	private int sortnum;
	private boolean secret = false;

	private transient Book book, trueProvider;
	private transient boolean checkedParent = false;
	private transient BookCategory parentCategory;
	private transient List<BookCategory> children = new ArrayList<>();
	private transient List<BookEntry> entries = new ArrayList<>();
	private transient boolean locked;
	private transient BookIcon icon = null;
	private transient ResourceLocation id;

	private transient boolean built;

	public ITextComponent getName() {
		return book.i18n ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	public String getDescription() {
		return description;
	}

	public BookIcon getIcon() {
		if (icon == null) {
			icon = BookIcon.from(iconRaw);
		}

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
		if (!checkedParent && !isRootCategory()) {
			if (parent.contains(":")) {
				parentCategory = book.contents.categories.get(new ResourceLocation(parent));
			} else {
				// if we are an extension, guess the extension book's domain first, then the parent book's domain
				if (isExtension()) {
					parentCategory = book.contents.categories.get(new ResourceLocation(trueProvider.getModNamespace(), parent));
				}

				if (parentCategory == null) {
					parentCategory = book.contents.categories.get(new ResourceLocation(book.getModNamespace(), parent));
				}
			}

			checkedParent = true;
		}

		return parentCategory;
	}

	public void updateLockStatus(boolean rootOnly) {
		if (rootOnly && !isRootCategory()) {
			return;
		}

		children.forEach((c) -> c.updateLockStatus(false));

		boolean wasLocked = locked;

		updateLocked: {
			locked = !children.isEmpty() || !entries.isEmpty(); // empty categories are unlocked by default
			for (BookCategory c : children) {
				if (!c.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}

			for (BookEntry e : entries) {
				if (!e.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}
		}

		if (!locked && wasLocked) {
			book.markUpdated();
		}
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
	}

	public boolean isLocked() {
		return getBook().advancementsEnabled() && locked;
	}

	public boolean isRootCategory() {
		return parent == null || parent.isEmpty();
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean canAdd() {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}

	@Override
	public int compareTo(BookCategory o) {
		if (!book.advancementsEnabled() && o.locked != this.locked) {
			return this.locked ? 1 : -1;
		}

		return this.sortnum - o.sortnum;
	}

	public void setBook(Book book) {
		if (book.isExtension) {
			this.book = book.extensionTarget;
			trueProvider = book;
		} else {
			this.book = book;
		}
	}

	public void build(ResourceLocation id) {
		if (built) {
			return;
		}

		this.id = id;
		BookCategory parent = getParentCategory();
		if (parent != null) {
			parent.addChildCategory(this);
		}

		built = true;
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

	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> entryStream = entries.stream().filter(e -> !e.isLocked()).map(BookEntry::getReadState);
		Stream<EntryDisplayState> childrenStream = children.stream().map(BookCategory::getReadState);
		return mostImportantState(Streams.concat(entryStream, childrenStream));
	}

	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();

		if (parentCategory != null) {
			parentCategory.markReadStateDirty();
		} else {
			book.contents.markReadStateDirty();
		}
	}

}
