package vazkii.patchouli.common.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final Map<Identifier, Book> books = new HashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
			.create();

	private boolean loaded = false;

	private BookRegistry() {}

	public void init() {
		Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
		Map<Pair<ModContainer, Identifier>, String> foundBooks = new HashMap<>();

		mods.forEach(mod -> {
			String id = mod.getMetadata().getId();
			findFiles(mod, String.format("data/%s/%s", id, BOOKS_LOCATION), (path) -> Files.exists(path),
					(base, file) -> {
						if (file.toString().endsWith("book.json")) {
							String fileStr = file.toString().replaceAll("\\\\", "/");
							String relPath = fileStr
									.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
							String bookName = relPath.substring(0, relPath.indexOf("/"));

							if (bookName.contains("/")) {
								Patchouli.LOGGER.warn("Ignored book.json @ {}", file);
								return true;
							}

							String assetPath = fileStr.substring(fileStr.indexOf("data/"));
							Identifier bookId = new Identifier(id, bookName);
							foundBooks.put(Pair.of(mod, bookId), assetPath);
						}

						return true;
					}, false, true);
		});

		foundBooks.forEach((pair, file) -> {
			ModContainer mod = pair.getLeft();
			Identifier res = pair.getRight();

			try (InputStream stream = Files.newInputStream(mod.getPath(file))) {
				loadBook(mod, res, stream, false);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
						res, mod.getMetadata().getId(), e);
			}
		});

		BookFolderLoader.findBooks();
	}

	public void loadBook(ModContainer mod, Identifier res, InputStream stream,
			boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = GSON.fromJson(reader, Book.class);
		book.build(mod, res, external);
		books.put(res, book);
	}

	@Environment(EnvType.CLIENT)
	public void reloadContents() {
		books.values().forEach(Book::reloadContents);
		books.values().forEach(Book::reloadExtensionContents);
		ClientBookRegistry.INSTANCE.reloadLocks(false);
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// HELPER

	public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor,
			BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot, boolean visitAllFiles) {
		if (mod.getMetadata().getId().equals("minecraft"))
			return false;

		boolean success = true;

		try {
			Path root = mod.getRootPath().resolve(base);

			if (!Files.exists(root))
				return defaultUnfoundRoot;

			if (preprocessor != null) {
				Boolean cont = preprocessor.apply(root);
				if (cont == null || !cont)
					return false;
			}

			if (processor != null) {
				Iterator<Path> itr = Files.walk(root).iterator();

				while (itr.hasNext()) {
					Boolean cont = processor.apply(root, itr.next());

					if (visitAllFiles)
						success &= cont != null && cont;
					else if (cont == null || !cont)
						return false;
				}
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		return success;
	}

}
