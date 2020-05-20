package vazkii.patchouli.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public abstract class PatchouliBookProvider implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;
	private final String locale;
	private final String modid;

	public PatchouliBookProvider(DataGenerator gen, String modid, String locale) {
		this.generator = gen;
		this.modid = modid;
		this.locale = locale;
	}

	/**
	 * Performs this provider's action.
	 *
	 * @param cache the cache
	 */
	@Override
	public void act(@Nonnull DirectoryCache cache) {
		addBooks(book -> {
			saveBook(cache, book.toJson(), book.getId());
			for (CategoryBuilder category : book.getCategories()) {
				saveCategory(cache, category.toJson(), book.getId(), category.getId());
				for (EntryBuilder entry : category.getEntries()) {
					saveEntry(cache, entry.toJson(), book.getId(), entry.getId());
				}
			}
		});
	}

	protected abstract void addBooks(Consumer<BookBuilder> consumer);

	private void saveEntry(DirectoryCache cache, JsonObject json, ResourceLocation bookId, ResourceLocation id) {
		Path mainOutput = generator.getOutputFolder();
		String pathSuffix = makeBookPath(bookId) + "/" + locale + "/entries/" + id.getPath() + ".json";
		Path outputPath = mainOutput.resolve(pathSuffix);
		try {
			IDataProvider.save(GSON, cache, json, outputPath);
		} catch (IOException e) {
			LOGGER.error("Couldn't save entry to {}", outputPath, e);
		}
	}

	private void saveCategory(DirectoryCache cache, JsonObject json, ResourceLocation bookId, ResourceLocation id) {
		Path mainOutput = generator.getOutputFolder();
		String pathSuffix = makeBookPath(bookId) + "/" + locale + "/categories/" + id.getPath() + ".json";
		Path outputPath = mainOutput.resolve(pathSuffix);
		try {
			IDataProvider.save(GSON, cache, json, outputPath);
		} catch (IOException e) {
			LOGGER.error("Couldn't save category to {}", outputPath, e);
		}
	}

	private void saveBook(DirectoryCache cache, JsonObject json, ResourceLocation bookId) {
		Path mainOutput = generator.getOutputFolder();
		String pathSuffix = makeBookPath(bookId) + "/book.json";
		Path outputPath = mainOutput.resolve(pathSuffix);
		try {
			IDataProvider.save(GSON, cache, json, outputPath);
		} catch (IOException e) {
			LOGGER.error("Couldn't save book to {}", outputPath, e);
		}
	}

	public BookBuilder createBookBuilder(String id, String name, String landingText) {
		return new BookBuilder(modid, id, name, landingText);
	}

	private String makeBookPath(ResourceLocation bookId) {
		return "data/" + bookId.getNamespace() + "/patchouli_books/" + bookId.getPath();
	}

	/**
	 * Gets a name for this provider, to use in logging.
	 */
	@Nonnull
	@Override
	public String getName() {
		return "Patchouli Book Provider";
	}
}
