package vazkii.patchouli.common.book;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.common.base.Patchouli;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final Map<ResourceLocation, Book> books = new HashMap<>();
	public Gson gson;

	private boolean loaded = false;

	private BookRegistry() {
		gson = new GsonBuilder().create();
	}

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
					}, false, true);
		});

		foundBooks.forEach((pair, file) -> {
			ModInfo mod = pair.getLeft();
			Optional<? extends ModContainer> container = ModList.get().getModContainerById(mod.getModId());
			container.ifPresent(c -> {
				ResourceLocation res = pair.getRight();

				Class<?> ownerClass = c.getMod().getClass();
				InputStream stream = ownerClass.getResourceAsStream(file);
				loadBook(mod, ownerClass, res, stream, false);
			});
		});

		BookFolderLoader.findBooks();
	}

	public void loadBook(IModInfo mod, Class<?> ownerClass, ResourceLocation res, InputStream stream,
			boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = gson.fromJson(reader, Book.class);

		books.put(res, book);
		book.build(mod, ownerClass, res, external);
	}

	@OnlyIn(Dist.CLIENT)
	public void reload() {
		books.values().forEach(Book::reloadContents);
		books.values().forEach(Book::reloadExtensionContents);
		ClientAdvancements.updateLockStatus(false);
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// HELPER

	public static boolean findFiles(ModInfo mod, String base, Function<Path, Boolean> preprocessor,
			BiFunction<Path, Path, Boolean> processor, boolean defaultUnfoundRoot, boolean visitAllFiles) {
		if (mod.getModId().equals("minecraft") || mod.getModId().equals("forge"))
			return false;

		File source = mod.getOwningFile().getFile().getFilePath().toFile();

		FileSystem fs = null;
		boolean success = true;

		try {
			Path root = null;

			if (source.isFile()) {
				try {
					fs = FileSystems.newFileSystem(source.toPath(), null);
					root = fs.getPath("/" + base);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (source.isDirectory())
				root = source.toPath().resolve(base);

			if (root == null || !Files.exists(root))
				return defaultUnfoundRoot;

			if (preprocessor != null) {
				Boolean cont = preprocessor.apply(root);
				if (cont == null || !cont.booleanValue())
					return false;
			}

			if (processor != null) {
				Iterator<Path> itr = null;
				try {
					itr = Files.walk(root).iterator();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				while (itr != null && itr.hasNext()) {
					Boolean cont = processor.apply(root, itr.next());

					if (visitAllFiles)
						success &= cont != null && cont;
					else if (cont == null || !cont)
						return false;
				}
			}
		} finally {
			IOUtils.closeQuietly(fs);
		}

		return success;
	}

}
