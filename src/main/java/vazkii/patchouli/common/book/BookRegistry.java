package vazkii.patchouli.common.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;

import java.io.*;
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
import java.util.function.Predicate;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final Map<ResourceLocation, Book> books = new HashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.create();

	private boolean loaded = false;

	private BookRegistry() {}

	public void init() {
		Collection<IModInfo> mods = ModList.get().getMods();
		Map<Pair<IModInfo, ResourceLocation>, String> foundBooks = new HashMap<>();

		mods.forEach(mod -> {
			String id = mod.getModId();
			findFiles(mod, String.format("data/%s/%s", id, BOOKS_LOCATION), Files::exists,
					(path, file) -> {
						if (Files.isRegularFile(file)
								&& file.getFileName().toString().equals("book.json")) {
							String fileStr = file.toString().replaceAll("\\\\", "/");
							String relPath = fileStr
									.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
							String bookName = relPath.substring(0, relPath.indexOf("/"));

							if (bookName.contains("/")) {
								Patchouli.LOGGER.warn("Ignored book.json @ {}", file);
								return true;
							}

							String assetPath = fileStr.substring(fileStr.indexOf("data/"));
							ResourceLocation bookId = new ResourceLocation(id, bookName);
							foundBooks.put(Pair.of(mod, bookId), assetPath);
						}

						return true;
					}, true, 2);
		});

		foundBooks.forEach((pair, file) -> {
			IModInfo mod = pair.getLeft();
			ResourceLocation res = pair.getRight();

			var modObject = ModList.get().getModFileById(mod.getModId()).getFile().findResource(file);
			try (InputStream stream = Files.newInputStream(modObject)) {
				loadBook(mod, res, stream, false);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
						res, mod.getModId(), e);
			}
		});

		BookFolderLoader.findBooks();

		for (Book book : books.values()) {
			if (book.useResourcePack && !book.allowExtensions) {
				throw new IllegalArgumentException(
						String.format("Book %s uses resource pack loading but doesn't allow extensions. "
								+ "All resource pack books allow extensions by definition.", book.id)
				);
			}
			if (book.isExtension) {
				book.extensionTarget = books.get(book.extend);

				if (book.extensionTarget == null) {
					throw new IllegalArgumentException("Extension Book " + book.id + " has no valid target");
				} else if (!book.extensionTarget.allowExtensions) {
					throw new IllegalArgumentException("Book " + book.extensionTarget.id + " doesn't allow extensions, so " + book.id + " can't modify it");
				} else if (book.useResourcePack) {
					Patchouli.LOGGER.warn("Book {} is a resource-pack-based book. Extension books are unnecessary for resource-pack-based books. "
							+ "You should simply create a resource pack with the extra content you want to add or override.",
							book.extensionTarget.id);
				}

				book.extensionTarget.extensions.add(book);
			}
		}
	}

	public void loadBook(IModInfo mod, ResourceLocation res, InputStream stream,
			boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = GSON.fromJson(reader, Book.class);
		book.build(mod, res, external);
		books.put(res, book);
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadContents(boolean resourcePackBooksOnly) {
		PatchouliConfig.reloadBuiltinFlags();
		for (Book book : books.values()) {
			if (resourcePackBooksOnly && !book.useResourcePack) {
				continue;
			}
			book.reloadContents();
		}
		ClientBookRegistry.INSTANCE.reloadLocks(false);
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// HELPER

	public static void findFiles(IModInfo mod, String base, Predicate<Path> rootFilter,
			BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) {
		findFiles(mod, base, rootFilter, processor, visitAllFiles, Integer.MAX_VALUE);
	}

	public static void findFiles(IModInfo mod, String base, Predicate<Path> rootFilter,
			BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) {
		if (mod.getModId().equals("minecraft")) {
			return;
		}

		Path source;
		if (mod.getOwningFile() instanceof ModFileInfo info) {
			source = info.getFile().getFilePath();
		} else {
			return;
		}

		try {
			if (Files.isRegularFile(source)) {
				try (FileSystem fs = FileSystems.newFileSystem(source)) {
					walk(fs.getPath("/" + base), rootFilter, processor, visitAllFiles, maxDepth);
				}
			} else {
				walk(source.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor,
			boolean visitAllFiles, int maxDepth) throws IOException {
		if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
			return;
		}

		if (processor != null) {
			Iterator<Path> itr = Files.walk(root, maxDepth).iterator();

			while (itr.hasNext()) {
				boolean cont = processor.apply(root, itr.next());

				if (!visitAllFiles && !cont) {
					return;
				}
			}
		}
	}

}
