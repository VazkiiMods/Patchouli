package vazkii.patchouli.client.book;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData;
import vazkii.patchouli.client.book.page.PageEmpty;
import vazkii.patchouli.client.book.page.PageQuest;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BookEntry extends AbstractReadStateHolder implements Comparable<BookEntry> {

	@SerializedName("name") private String name;
	@SerializedName("category") private String category;
	@SerializedName("flag") private String flag;

	@SerializedName("icon") private String iconRaw;

	@SerializedName("priority") private boolean priority = false;
	@SerializedName("secret") private boolean secret = false;
	@SerializedName("read_by_default") private boolean readByDefault = false;
	@SerializedName("pages") private BookPage[] pages;
	@SerializedName("advancement") private String advancement;
	@SerializedName("turnin") private String turnin;
	@SerializedName("sortnum") private int sortnum;
	@SerializedName("entry_color") private String entryColorRaw;

	@SerializedName("extra_recipe_mappings") private Map<String, Integer> extraRecipeMappings;

	private transient ResourceLocation id;
	private transient Book book;
	private transient BookCategory lcategory = null;
	private transient BookIcon icon = null;
	private final transient List<BookPage> realPages = new ArrayList<>();
	private final transient List<StackWrapper> relevantStacks = new LinkedList<>();
	private transient boolean locked;
	private transient int entryColor;

	private transient boolean built;

	public MutableComponent getName() {
		return book.i18n ? new TranslatableComponent(name) : new TextComponent(name);
	}

	public List<BookPage> getPages() {
		List<BookPage> pages = !getBook().advancementsEnabled() ? realPages : realPages.stream().filter(BookPage::isPageUnlocked).collect(Collectors.toList());

		return pages.isEmpty() ? NO_PAGE : pages;
	}

	public int getPageFromAnchor(String anchor) {
		List<BookPage> pages = getPages();
		for (int i = 0; i < pages.size(); i++) {
			BookPage page = pages.get(i);
			if (anchor.equals(page.anchor)) {
				return i;
			}
		}

		return -1;
	}

	private static final List<BookPage> NO_PAGE = ImmutableList.of(new PageEmpty());

	public boolean isPriority() {
		return priority;
	}

	public BookIcon getIcon() {
		if (icon == null) {
			icon = BookIcon.from(iconRaw);
		}

		return icon;
	}

	public void initCategory(Function<ResourceLocation, BookCategory> categories) {
		if (lcategory == null) {
			if (category.contains(":")) { // full category ID
				lcategory = categories.apply(new ResourceLocation(category));
			} else {
				String hint = String.format("`%s:%s`", book.getModNamespace(), category);
				throw new IllegalArgumentException("`category` must be fully qualified (domain:name). Hint: Try " + hint);
			}
		}
	}

	public BookCategory getCategory() {
		return lcategory;
	}

	public void updateLockStatus() {
		boolean currLocked = locked;
		locked = advancement != null && !advancement.isEmpty() && !ClientAdvancements.hasDone(advancement);

		boolean dirty = false;
		if (!locked && currLocked) {
			dirty = true;
			book.markUpdated();
		}

		if (!dirty && !readStateDirty && getReadState() == EntryDisplayState.PENDING && ClientAdvancements.hasDone(turnin)) {
			dirty = true;
		}

		if (dirty) {
			markReadStateDirty();
		}
	}

	public boolean isLocked() {
		if (isSecret()) {
			return locked;
		}
		return getBook().advancementsEnabled() && locked;
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
	}

	public int getEntryColor() {
		return entryColor;
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean canAdd() {
		return (flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag));
	}

	public boolean isFoundByQuery(String query) {
		if (getName().getString().toLowerCase().contains(query)) {
			return true;
		}

		for (StackWrapper wrapper : relevantStacks) {
			if (wrapper.stack().getHoverName().getString().toLowerCase().contains(query)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int compareTo(BookEntry o) {
		if (o.locked != this.locked) {
			return this.locked ? 1 : -1;
		}

		EntryDisplayState ourState = getReadState();
		EntryDisplayState otherState = o.getReadState();

		if (ourState != otherState) {
			return ourState.compareTo(otherState);
		}

		if (o.priority != this.priority) {
			return this.priority ? -1 : 1;
		}

		int sort = this.sortnum - o.sortnum;

		return sort == 0 ? this.getName().getString().compareTo(o.getName().getString()) : sort;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public void setId(ResourceLocation id) {
		this.id = id;
	}

	public void build(BookContentsBuilder builder) {
		if (built) {
			return;
		}

		if (entryColorRaw != null) {
			this.entryColor = Integer.parseInt(entryColorRaw, 16);
		} else {
			this.entryColor = book.textColor;
		}
		for (int i = 0; i < pages.length; i++) {
			if (pages[i].canAdd(book)) {
				try {
					pages[i].build(this, builder, i);
					realPages.add(pages[i]);
				} catch (Exception e) {
					throw new RuntimeException("Error while loading entry " + id + " page " + i, e);
				}
			}
		}

		if (extraRecipeMappings != null) {
			for (Entry<String, Integer> entry : extraRecipeMappings.entrySet()) {
				String key = entry.getKey();
				List<ItemStack> stacks;
				int pageNumber = entry.getValue();
				try {
					stacks = ItemStackUtil.loadStackListFromString(key);
				} catch (Exception e) {
					Patchouli.LOGGER.warn("Invalid extra recipe mapping: {} to page {} in entry {}: {}", key, pageNumber, id, e.getMessage());
					continue;
				}
				if (!stacks.isEmpty() && pageNumber < pages.length) {
					for (ItemStack stack : stacks) {
						addRelevantStack(builder, stack, pageNumber);
					}
				} else {
					Patchouli.LOGGER.warn("Invalid extra recipe mapping: {} to page {} in entry {}: Empty entry or page out of bounds", key, pageNumber, id);
				}
			}
		}

		built = true;
	}

	public void addRelevantStack(BookContentsBuilder builder, ItemStack stack, int page) {
		if (stack.isEmpty()) {
			return;
		}
		StackWrapper wrapper = ItemStackUtil.wrapStack(stack);
		relevantStacks.add(wrapper);

		builder.addRecipeMapping(wrapper, this, page / 2);
	}

	/**
	 * @return The logical book this entry belongs to.
	 *         For entries added by extension books, this is the book being extended.
	 */
	public final Book getBook() {
		return book;
	}

	@Override
	protected EntryDisplayState computeReadState() {
		BookData data = PersistentData.data.getBookData(book);
		if (data != null && getId() != null && !readByDefault && !isLocked() && !data.viewedEntries.contains(getId().toString())) {
			return EntryDisplayState.UNREAD;
		}

		if (turnin != null && !turnin.isEmpty() && !ClientAdvancements.hasDone(turnin)) {
			return EntryDisplayState.PENDING;
		}

		for (BookPage page : pages) {
			if (page instanceof PageQuest && ((PageQuest) page).isCompleted(book)) {
				return EntryDisplayState.COMPLETED;
			}
		}

		return EntryDisplayState.NEUTRAL;
	}

	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();
		getCategory().markReadStateDirty();
	}

}
