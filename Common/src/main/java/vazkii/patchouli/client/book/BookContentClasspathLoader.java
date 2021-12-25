package vazkii.patchouli.client.book;

import net.minecraft.resources.ResourceLocation;

import org.apache.commons.io.FilenameUtils;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.xplat.XplatModContainer;

import javax.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

/**
 * BookContentLoader that loads from the classpath of the owning mod.
 * This is the legacy behaviour of 1.16 and below.
 */
public final class BookContentClasspathLoader implements BookContentLoader {
	public static final BookContentClasspathLoader INSTANCE = new BookContentClasspathLoader();

	private BookContentClasspathLoader() {}

	private BiFunction<Path, Path, Boolean> pred(String modId, List<ResourceLocation> list) {
		return (root, file) -> {
			Path rel = root.relativize(file);
			String relName = rel.toString();
			if (relName.endsWith(".json")) {
				relName = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(relName));
				ResourceLocation res = new ResourceLocation(modId, relName);
				list.add(res);
			}

			return true;
		};
	}

	@Override
	public void findFiles(Book book, String dir, List<ResourceLocation> list) {
		XplatModContainer mod = book.owner;
		String id = mod.getId();
		BookRegistry.findFiles(mod, String.format("data/%s/%s/%s/%s/%s", id, BookRegistry.BOOKS_LOCATION, book.id.getPath(), BookContentsBuilder.DEFAULT_LANG, dir), path -> true, pred(id, list), false);
	}

	@Nullable
	@Override
	public InputStream loadJson(Book book, ResourceLocation resloc, @Nullable ResourceLocation fallback) {
		String path = "data/" + resloc.getNamespace() + "/" + resloc.getPath();
		PatchouliAPI.LOGGER.debug("Loading {}", path);

		try {
			return Files.newInputStream(book.owner.getPath(path));
		} catch (IOException ex) {
			if (fallback != null) {
				return loadJson(book, fallback, null);
			} else {
				PatchouliAPI.LOGGER.warn("Failed to load {}.", resloc, ex);
				return null;
			}
		}
	}
}
