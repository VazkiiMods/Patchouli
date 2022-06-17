package vazkii.patchouli.client.book;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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
				category.build(this);
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

		BookCategory pamphletCategory = null;
		if (book.isPamphlet) {
			if (categories.size() != 1) {
				throw new RuntimeException("A pamphlet should have exactly one category but instead there were " + categories.size());
			}
			pamphletCategory = categories.values().iterator().next();
		}

		return new BookContents(
				book,
				ImmutableMap.copyOf(categories),
				ImmutableMap.copyOf(entries),
				ImmutableMap.copyOf(recipeMappings),
				pamphletCategory
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
		JsonElement json = loadLocalizedJson(book, loader, file);
		var category = new BookCategory(json.getAsJsonObject(), id, book);
		if (category.canAdd()) {
			return category;
		}
		return null;
	}

	@Nullable
	private static BookEntry loadEntry(Book book, BookContentLoader loader, ResourceLocation id,
			ResourceLocation file, Function<ResourceLocation, BookCategory> categories) {
		JsonElement json = loadLocalizedJson(book, loader, file);
		var entry = new BookEntry(json.getAsJsonObject(), id, file, book, categories);

		if (entry.canAdd()) {
			entry.getCategory().addEntry(entry);
			return entry;
		}
		return null;
	}

	private static Supplier<BookTemplate> loadTemplate(Book book, BookContentLoader loader, ResourceLocation key, ResourceLocation res) {
		JsonElement json = loadLocalizedJson(book, loader, res);

		Supplier<BookTemplate> supplier = () -> ClientBookRegistry.INSTANCE.gson.fromJson(json, BookTemplate.class);

		// test supplier
		BookTemplate template = supplier.get();
		if (template == null) {
			throw new IllegalArgumentException(res + " could not be instantiated by the supplier.");
		}

		return supplier;
	}

	private static JsonElement loadLocalizedJson(Book book, BookContentLoader loader, ResourceLocation res) {
		ResourceLocation localized = new ResourceLocation(res.getNamespace(),
				res.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		JsonElement input = loader.loadJson(book, localized, res);
		if (input == null) {
			throw new IllegalArgumentException(res + " does not exist.");
		}

		return input;
	}
}
