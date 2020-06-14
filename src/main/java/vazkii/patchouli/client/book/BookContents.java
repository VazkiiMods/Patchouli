package vazkii.patchouli.client.book;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.apache.commons.io.FilenameUtils;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.GuiBookLanding;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookContents extends AbstractReadStateHolder {

	protected static final String DEFAULT_LANG = "en_us";

	public static final Map<Identifier, Supplier<BookTemplate>> addonTemplates = new ConcurrentHashMap<>();

	public final Book book;

	public Map<Identifier, BookCategory> categories = new HashMap<>();
	public Map<Identifier, BookEntry> entries = new HashMap<>();
	public Map<Identifier, Supplier<BookTemplate>> templates = new HashMap<>();
	public Map<StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap<>();
	private boolean errored = false;
	private Exception exception = null;

	public Deque<GuiBook> guiStack = new ArrayDeque<>();
	public GuiBook currentGui;

	public BookIcon indexIcon;

	public BookContents(Book book) {
		this.book = book;
	}

	public boolean isErrored() {
		return errored;
	}

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

	public void reload(boolean isOverride) {
		errored = false;

		if (!isOverride) {
			currentGui = null;
			guiStack.clear();
			categories.clear();
			entries.clear();
			templates.clear();
			recipeMappings.clear();

			templates.putAll(addonTemplates);

			if (book.indexIconRaw == null || book.indexIconRaw.isEmpty()) {
				indexIcon = new BookIcon(book.getBookItem());
			} else {
				indexIcon = BookIcon.from(book.indexIconRaw);
			}
		}

		List<Identifier> foundCategories = new ArrayList<>();
		List<Identifier> foundEntries = new ArrayList<>();
		List<Identifier> foundTemplates = new ArrayList<>();

		try {
			String bookName = book.id.getPath();

			findFiles("categories", foundCategories);
			findFiles("entries", foundEntries);
			findFiles("templates", foundTemplates);

			foundCategories.forEach(c -> loadCategory(c, new Identifier(c.getNamespace(),
					String.format("%s/%s/%s/categories/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, c.getPath())), book));
			foundEntries.stream().map(id -> loadEntry(id, new Identifier(id.getNamespace(),
					String.format("%s/%s/%s/entries/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, id.getPath())), book))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(b -> entries.put(b.getId(), b));
			foundTemplates.forEach(e -> loadTemplate(e, new Identifier(e.getNamespace(),
					String.format("%s/%s/%s/templates/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, e.getPath()))));

			categories.forEach((id, category) -> {
				try {
					category.build(id);
				} catch (Exception e) {
					throw new RuntimeException("Error while building category " + id, e);
				}
			});

			entries.values().forEach(entry -> {
				try {
					entry.build();
				} catch (Exception e) {
					throw new RuntimeException("Error building entry " + entry.getId(), e);
				}
			});
		} catch (Exception e) {
			exception = e;
			errored = true;
			Patchouli.LOGGER.error("Error while loading contents for book {}", book.id, e);
		}
	}

	protected void findFiles(String dir, List<Identifier> list) {
		ModContainer mod = book.owner;
		String id = mod.getMetadata().getId();
		BookRegistry.findFiles(mod, String.format("data/%s/%s/%s/%s/%s", id, BookRegistry.BOOKS_LOCATION, book.id.getPath(), DEFAULT_LANG, dir), path -> true, pred(id, list), false);
	}

	private BiFunction<Path, Path, Boolean> pred(String modId, List<Identifier> list) {
		return (root, file) -> {
			Path rel = root.relativize(file);
			String relName = rel.toString();
			if (relName.endsWith(".json")) {
				relName = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(relName));
				Identifier res = new Identifier(modId, relName);
				list.add(res);
			}

			return true;
		};
	}

	private void loadCategory(Identifier key, Identifier res, Book book) {
		try (Reader stream = loadLocalizedJson(res)) {
			BookCategory category = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookCategory.class);
			if (category == null) {
				throw new IllegalArgumentException(res + " does not exist.");
			}

			category.setBook(book);
			if (category.canAdd()) {
				categories.put(key, category);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private Optional<BookEntry> loadEntry(Identifier id, Identifier file, Book book) {
		try (Reader stream = loadLocalizedJson(file)) {
			BookEntry entry = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookEntry.class);
			if (entry == null) {
				throw new IllegalArgumentException(file + " does not exist.");
			}

			entry.setBook(book);
			if (entry.canAdd()) {
				BookCategory category = entry.getCategory();
				if (category != null) {
					category.addEntry(entry);
				} else {
					String msg = String.format("Entry in file %s does not have a valid category.", file);
					throw new RuntimeException(msg);
				}

				entry.setId(id);
				return Optional.of(entry);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return Optional.empty();
	}

	private void loadTemplate(Identifier key, Identifier res) {
		String json;
		try (BufferedReader stream = loadLocalizedJson(res)) {
			json = stream.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		Supplier<BookTemplate> supplier = () -> ClientBookRegistry.INSTANCE.gson.fromJson(json, BookTemplate.class);

		// test supplier
		BookTemplate template = supplier.get();
		if (template == null) {
			throw new IllegalArgumentException(res + " could not be instantiated by the supplier.");
		}

		templates.put(key, supplier);
	}

	private BufferedReader loadLocalizedJson(Identifier res) {
		Identifier localized = new Identifier(res.getNamespace(),
				res.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		InputStream input = loadJson(localized, res);
		if (input == null) {
			throw new IllegalArgumentException(res + " does not exist.");
		}

		return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
	}

	protected InputStream loadJson(Identifier resloc, Identifier fallback) {
		String path = "data/" + resloc.getNamespace() + "/" + resloc.getPath();
		Patchouli.LOGGER.debug("Loading {}", path);

		try {
			return Files.newInputStream(book.owner.getPath(path));
		} catch (IOException ex) {
			if (fallback != null) {
				Patchouli.LOGGER.warn("Failed to load " + resloc + ". Switching to fallback.");
				return loadJson(fallback, null);
			}
		}

		return null;
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

			entry.getBook().contents.guiStack.push(prevGui);
		}
	}

}
