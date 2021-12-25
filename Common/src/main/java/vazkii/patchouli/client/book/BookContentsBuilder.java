package vazkii.patchouli.client.book;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Holds book content state while it is being assembled from JSON data and extension data.
 */
public class BookContentsBuilder {
	public static final String DEFAULT_LANG = "en_us";
	private final Map<ResourceLocation, BookCategory> categories = new HashMap<>();
	private final Map<ResourceLocation, BookEntry> entries = new HashMap<>();
	private final Map<ResourceLocation, Supplier<BookTemplate>> templates = new HashMap<>();
	private final Map<ItemStackUtil.StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap<>();

	private final boolean singleBookReload;

	private interface LoadFunc<T> {
		@Nullable
		T load(Book book, BookContentLoader loader, ResourceLocation id, ResourceLocation file);
	}

	/**
	 * @param singleBookReload Signals that the book was reloaded through the ingame button
	 *                         and should bypass the cached resource reload data.
	 */
	public BookContentsBuilder(boolean singleBookReload) {
		this.singleBookReload = singleBookReload;
		templates.putAll(BookContents.addonTemplates);
	}

	public BookContentsBuilder() {
		this(false);
	}

	@Nullable
	public BookCategory getCategory(ResourceLocation id) {
		return categories.get(id);
	}

	@Nullable
	public BookEntry getEntry(ResourceLocation id) {
		return entries.get(id);
	}

	@Nullable
	public Supplier<BookTemplate> getTemplate(ResourceLocation id) {
		return templates.get(id);
	}

	public void addRecipeMapping(ItemStackUtil.StackWrapper stack, BookEntry entry, int spread) {
		recipeMappings.put(stack, Pair.of(entry, spread));
	}

	/**
	 * Contribute the on-disk contents of the given book to this builder.
	 * Should first be called with the "real" book, then by all extensions
	 */
	public void loadFrom(Book book) {
		load(book, "categories", BookContentsBuilder::loadCategory, categories);
		load(book, "entries",
				(b, l, id, file) -> BookContentsBuilder.loadEntry(b, l, id, file, categories::get),
				entries);
		load(book, "templates", BookContentsBuilder::loadTemplate, templates);
	}

	public BookContents build(Book book) {
		categories.forEach((id, category) -> {
			try {
				category.build(id, this);
			} catch (Exception e) {
				throw new RuntimeException("Error while building category " + id, e);
			}
		});

		entries.values().forEach(entry -> {
			try {
				entry.build(this);
			} catch (Exception e) {
				throw new RuntimeException("Error building entry " + entry.getId(), e);
			}
		});

		return new BookContents(
				book,
				ImmutableMap.copyOf(categories),
				ImmutableMap.copyOf(entries),
				ImmutableMap.copyOf(recipeMappings)
		);
	}

	private <T> void load(Book book, String thing, LoadFunc<T> loader, Map<ResourceLocation, T> builder) {
		BookContentLoader contentLoader = getContentLoader(book);
		List<ResourceLocation> foundIds = new ArrayList<>();
		contentLoader.findFiles(book, thing, foundIds);

		for (ResourceLocation id : foundIds) {
			String filePath = String.format("%s/%s/%s/%s/%s.json",
					BookRegistry.BOOKS_LOCATION, book.id.getPath(), DEFAULT_LANG, thing, id.getPath());
			T value = loader.load(book, contentLoader, id, new ResourceLocation(id.getNamespace(), filePath));
			if (value != null) {
				builder.put(id, value);
			}
		}
	}

	protected BookContentLoader getContentLoader(Book book) {
		if (book.isExternal) {
			return BookContentExternalLoader.INSTANCE;
		}
		if (book.useResourcePack) {
			// A quick reload should not reuse stale data, it is initiated by the user anyways
			return singleBookReload
					? BookContentResourceDirectLoader.INSTANCE
					: BookContentResourceListenerLoader.INSTANCE;
		}
		return BookContentClasspathLoader.INSTANCE;
	}

	@Nullable
	private static BookCategory loadCategory(Book book, BookContentLoader loader, ResourceLocation id, ResourceLocation file) {
		try (Reader stream = loadLocalizedJson(book, loader, file)) {
			BookCategory category = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookCategory.class);
			if (category == null) {
				throw new IllegalArgumentException(file + " does not exist.");
			}

			category.setBook(book);
			if (category.canAdd()) {
				return category;
			}
			return null;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	@Nullable
	private static BookEntry loadEntry(Book book, BookContentLoader loader, ResourceLocation id,
			ResourceLocation file, Function<ResourceLocation, BookCategory> categories) {
		try (Reader stream = loadLocalizedJson(book, loader, file)) {
			BookEntry entry = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookEntry.class);
			if (entry == null) {
				throw new IllegalArgumentException(file + " does not exist.");
			}

			entry.setBook(book);
			if (entry.canAdd()) {
				entry.initCategory(categories);

				BookCategory category = entry.getCategory();
				if (category != null) {
					category.addEntry(entry);
				} else {
					String msg = String.format("Entry in file %s does not have a valid category.", file);
					throw new RuntimeException(msg);
				}

				entry.setId(id);
				return entry;
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return null;
	}

	private static Supplier<BookTemplate> loadTemplate(Book book, BookContentLoader loader, ResourceLocation key, ResourceLocation res) {
		String json;
		try (BufferedReader stream = loadLocalizedJson(book, loader, res)) {
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

		return supplier;
	}

	private static BufferedReader loadLocalizedJson(Book book, BookContentLoader loader, ResourceLocation res) {
		ResourceLocation localized = new ResourceLocation(res.getNamespace(),
				res.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		InputStream input = loader.loadJson(book, localized, res);
		if (input == null) {
			throw new IllegalArgumentException(res + " does not exist.");
		}

		return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
	}
}
