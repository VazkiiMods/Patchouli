package vazkii.patchouli.client.book;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.GuiBookLanding;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

import javax.annotation.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BookContents extends AbstractReadStateHolder {

	public static final Map<Identifier, Supplier<BookTemplate>> addonTemplates = new ConcurrentHashMap<>();

	private final Book book;

	public final Map<Identifier, BookCategory> categories;
	public final Map<Identifier, BookEntry> entries;
	private final Map<StackWrapper, Pair<BookEntry, Integer>> recipeMappings;
	private final boolean errored;
	@Nullable private final Exception exception;

	public final Deque<GuiBook> guiStack = new ArrayDeque<>();
	public GuiBook currentGui = null;

	private BookContents(Book book, @Nullable Exception e) {
		this.book = book;
		this.errored = e != null;
		this.exception = e;
		this.categories = Collections.emptyMap();
		this.entries = Collections.emptyMap();
		this.recipeMappings = Collections.emptyMap();
	}

	public static BookContents empty(Book book, @Nullable Exception e) {
		return new BookContents(book, e);
	}

	public BookContents(Book book,
			ImmutableMap<Identifier, BookCategory> categories,
			ImmutableMap<Identifier, BookEntry> entries,
			ImmutableMap<StackWrapper, Pair<BookEntry, Integer>> recipeMappings) {
		this.book = book;
		this.categories = categories;
		this.entries = entries;
		this.recipeMappings = recipeMappings;
		this.errored = false;
		this.exception = null;
	}

	public boolean isErrored() {
		return errored;
	}

	@Nullable
	public Exception getException() {
		return exception;
	}

	public Pair<BookEntry, Integer> getEntryForStack(ItemStack stack) {
		return recipeMappings.get(ItemStackUtil.wrapStack(stack));
	}

	public GuiBook getCurrentGui() {
		if (currentGui == null) {
			currentGui = new GuiBookLanding(book);
		}

		return currentGui;
	}

	public void openLexiconGui(GuiBook gui, boolean push) {
		if (gui.canBeOpened()) {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (push && mc.currentScreen instanceof GuiBook && gui != mc.currentScreen) {
				guiStack.push((GuiBook) mc.currentScreen);
			}

			mc.openScreen(gui);
			gui.onFirstOpened();
		}
	}

	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> stream = categories.values().stream().filter(BookCategory::isRootCategory).map(BookCategory::getReadState);
		return mostImportantState(stream);
	}

	public final void checkValidCurrentEntry() {
		if (!getCurrentGui().canBeOpened()) {
			currentGui = null;
			guiStack.clear();
		}
	}

	/**
	 * Set the given entry to be one on top of the stack, i.e. will be shown next time the book is opened
	 */
	public final void setTopEntry(Identifier entryId, int page) {
		BookEntry entry = entries.get(entryId);
		if (!entry.isLocked()) {
			GuiBook prevGui = getCurrentGui();
			int spread = page / 2;
			currentGui = new GuiBookEntry(book, entry, spread);

			if (prevGui instanceof GuiBookEntry) {
				GuiBookEntry currEntry = (GuiBookEntry) prevGui;
				if (currEntry.getEntry() == entry && currEntry.getSpread() == spread) {
					return;
				}
			}

			entry.getBook().getContents().guiStack.push(prevGui);
		}
	}

}
