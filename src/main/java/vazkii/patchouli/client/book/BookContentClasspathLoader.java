package vazkii.patchouli.client.book;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import org.apache.commons.io.FilenameUtils;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

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
		IModInfo mod = book.owner;
		String id = mod.getModId();
		BookRegistry.findFiles(mod, String.format("data/%s/%s/%s/%s/%s", id, BookRegistry.BOOKS_LOCATION, book.id.getPath(), BookContentsBuilder.DEFAULT_LANG, dir), path -> true, pred(id, list), false);
	}

	@Nullable
	@Override
	public InputStream loadJson(Book book, ResourceLocation resloc, @Nullable ResourceLocation fallback) {
		String path = "data/" + resloc.getNamespace() + "/" + resloc.getPath();
		Patchouli.LOGGER.debug("Loading {}", path);

		Object modObject = ModList.get().getModObjectById(book.owner.getModId()).get();
		InputStream stream = modObject.getClass().getResourceAsStream(path);
		if (stream != null) {
			return stream;
		}

		if (fallback != null) {
			Patchouli.LOGGER.debug("Failed to load {}. Switching to fallback.", resloc);
			return loadJson(book, fallback, null);
		} else {
			Patchouli.LOGGER.warn("Failed to load {}.", resloc);
			return null;
		}
	}
}
