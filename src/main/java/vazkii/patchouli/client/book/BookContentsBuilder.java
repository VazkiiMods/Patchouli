package vazkii.patchouli.client.book;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

	private interface LoadFunc<T> {
		@Nullable
		T load(Book book, ResourceManager resourceManager, BookContentLoader loader, ResourceLocation id, ResourceLocation file);
	}

	public BookContentsBuilder() {
		templates.putAll(BookContents.addonTemplates);
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
	public void loadFrom(Book book, ResourceManager resourceManager) {
		load(book, resourceManager, "categories", BookContentsBuilder::loadCategory, categories);
		load(book, resourceManager, "entries",
				(b, rm, l, id, file) -> BookContentsBuilder.loadEntry(b, rm, l, id, file, categories::get),
				entries);
		load(book, resourceManager, "templates", BookContentsBuilder::loadTemplate, templates);
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

	private <T> void load(Book book, ResourceManager resourceManager, String dir, LoadFunc<T> loader, Map<ResourceLocation, T> builder) {
		BookContentLoader contentLoader = BookContentResourceLoader.INSTANCE;
		String prefix = String.format("%s/%s/%s/%s", BookRegistry.BOOKS_LOCATION, book.getId().getPath(), BookContentsBuilder.DEFAULT_LANG, dir);
		resourceManager.listResources(prefix, p -> p.endsWith(".json"))
				.stream()
				.distinct()
				.filter(file -> file.getNamespace().equals(book.getId().getNamespace()))
				.forEach(file -> {
					// caller expects list to contain logical id's, not file paths.
					// we end up going from path -> id -> back to path, but it's okay as a transitional measure
					Preconditions.checkArgument(file.getPath().startsWith(prefix));
					Preconditions.checkArgument(file.getPath().endsWith(".json"));
					String newPath = file.getPath().substring(prefix.length(), file.getPath().length() - ".json".length());
					// Vanilla expects `prefix` above to not have a trailing slash, so we
					// have to remove it ourselves from the path
					if (newPath.startsWith("/")) {
						newPath = newPath.substring(1);
					}
					var id = new ResourceLocation(file.getNamespace(), newPath);
					T value = loader.load(book, resourceManager, contentLoader, id, file);
					if (value != null) {
						builder.put(id, value);
					}
				});
	}

	@Nullable
	private static BookCategory loadCategory(Book book, ResourceManager resourceManager, BookContentLoader loader, ResourceLocation id, ResourceLocation file) {
		try (Reader stream = loadLocalizedJson(book, resourceManager, loader, file)) {
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
	private static BookEntry loadEntry(Book book, ResourceManager resourceManager, BookContentLoader loader, ResourceLocation id,
			ResourceLocation file, Function<ResourceLocation, BookCategory> categories) {
		try (Reader stream = loadLocalizedJson(book, resourceManager, loader, file)) {
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

	private static Supplier<BookTemplate> loadTemplate(Book book, ResourceManager resourceManager, BookContentLoader loader, ResourceLocation key, ResourceLocation res) {
		String json;
		try (BufferedReader stream = loadLocalizedJson(book, resourceManager, loader, res)) {
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

	private static BufferedReader loadLocalizedJson(Book book, final ResourceManager resourceManager, BookContentLoader loader, ResourceLocation res) {
		ResourceLocation localized = new ResourceLocation(res.getNamespace(),
				res.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		InputStream input = loader.loadJson(book, resourceManager, localized, res);
		if (input == null) {
			throw new IllegalArgumentException(res + " does not exist.");
		}

		return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
	}
}
