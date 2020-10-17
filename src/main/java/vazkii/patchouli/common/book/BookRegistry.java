package vazkii.patchouli.common.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final CountDownLatch loadingBarrier = new CountDownLatch(1);
	public final Map<ResourceLocation, Book> books = new HashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.create();

	private boolean loaded = false;

	private BookRegistry() {}

	public void init() {
		List<ModInfo> mods = ModList.get().getMods();
		Map<Pair<ModInfo, ResourceLocation>, String> foundBooks = new HashMap<>();

		mods.forEach(mod -> {
			String id = mod.getModId();
			findFiles(mod, String.format("data/%s/%s", id, BOOKS_LOCATION), (path) -> Files.exists(path),
					(path, file) -> {
						if (file.toString().endsWith("book.json")) {
							String fileStr = file.toString().replaceAll("\\\\", "/");
							String relPath = fileStr
									.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
							String bookName = relPath.substring(0, relPath.indexOf("/"));

							if (bookName.contains("/")) {
								Patchouli.LOGGER.warn("Ignored book.json @ {}", file);
								return true;
							}

							String assetPath = fileStr.substring(fileStr.indexOf("/data"));
							ResourceLocation bookId = new ResourceLocation(id, bookName);
							foundBooks.put(Pair.of(mod, bookId), assetPath);
						}

						return true;
					}, true);
		});

		foundBooks.forEach((pair, file) -> {
			ModInfo mod = pair.getLeft();
			Optional<? extends ModContainer> container = ModList.get().getModContainerById(mod.getModId());
			container.ifPresent(c -> {
				ResourceLocation res = pair.getRight();

				Class<?> ownerClass = c.getMod().getClass();
				try (InputStream stream = ownerClass.getResourceAsStream(file)) {
					loadBook(mod, ownerClass, res, stream, false);
				} catch (Exception e) {
					Patchouli.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
							res, c.getModInfo().getModId(), e);
				}
			});
		});

		BookFolderLoader.findBooks();
		loadingBarrier.countDown();
	}

	public void loadBook(IModInfo mod, Class<?> ownerClass, ResourceLocation res, InputStream stream,
			boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = GSON.fromJson(reader, Book.class);
		book.build(mod, ownerClass, res, external);
		books.put(res, book);
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadContents() {
		PatchouliConfig.reloadBuiltinFlags();
		books.values().forEach(Book::reloadContents);
		books.values().forEach(Book::reloadExtensionContents);
		ClientBookRegistry.INSTANCE.reloadLocks(false);
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// HELPER

	public static void findFiles(IModInfo mod, String base, Predicate<Path> rootFilter,
			BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) {
		if (mod.getModId().equals("minecraft") || mod.getModId().equals("forge")) {
			return;
		}

		IModFileInfo info = mod.getOwningFile();
		Path source;

		if (info instanceof ModFileInfo) {
			source = ((ModFileInfo) info).getFile().getFilePath();
		} else {
			return;
		}

		try {
			if (Files.isRegularFile(source)) {
				try (FileSystem fs = FileSystems.newFileSystem(source, null)) {
					walk(fs.getPath("/" + base), rootFilter, processor, visitAllFiles);
				}
			} else {
				walk(source.resolve(base), rootFilter, processor, visitAllFiles);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) throws IOException {
		if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
			return;
		}

		if (processor != null) {
			Iterator<Path> itr = Files.walk(root).iterator();

			while (itr.hasNext()) {
				boolean cont = processor.apply(root, itr.next());

				if (!visitAllFiles && !cont) {
					return;
				}
			}
		}
	}

}
