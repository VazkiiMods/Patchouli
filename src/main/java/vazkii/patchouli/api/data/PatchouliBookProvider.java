package vazkii.patchouli.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2020-02-26
 */
public abstract class PatchouliBookProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final List<BookBuilder> books = new ArrayList<>();

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
        books.clear();
        addBooks();
        for (BookBuilder book : books) {
            saveBook(cache, book.toJson(), book.getId());
            for (CategoryBuilder category : book.categories) {
                saveCategory(cache, category.toJson(), book.getId(), category.getId());
                for (EntryBuilder entry : category.entries) {
                    saveEntry(cache, entry.toJson(), book.getId(), entry.getId());
                }
            }
        }
    }

    protected abstract void addBooks();

    private void saveEntry(DirectoryCache cache, JsonObject json, String bookName, String id) {
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + modid + "/patchouli_books/" + bookName + "/" + locale + "/entries/" + id + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            IDataProvider.save(GSON, cache, json, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save entry to {}", outputPath, e);
        }
    }

    private void saveCategory(DirectoryCache cache, JsonObject json, String bookName, String id) {
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + modid + "/patchouli_books/" + bookName + "/" + locale + "/categories/" + id + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            IDataProvider.save(GSON, cache, json, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save category to {}", outputPath, e);
        }
    }

    private void saveBook(DirectoryCache cache, JsonObject json, String name) {
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + modid + "/patchouli_books/" + name + "/" + locale + "/book.json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            IDataProvider.save(GSON, cache, json, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save book to {}", outputPath, e);
        }
    }

    public BookBuilder addBook(String id, String name, String landingText) {
        BookBuilder builder = new BookBuilder(modid, id, name, landingText);
        books.add(builder);
        return builder;
    }

    public TranslationBookBuilder addTranslationBook(String id, String name, String landingText) {
        TranslationBookBuilder builder = new TranslationBookBuilder(modid, id);
        books.add(builder);
        return builder;
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
