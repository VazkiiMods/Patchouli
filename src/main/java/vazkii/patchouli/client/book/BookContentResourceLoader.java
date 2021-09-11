package vazkii.patchouli.client.book;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class BookContentResourceLoader implements BookContentLoader {
	public static final BookContentResourceLoader INSTANCE = new BookContentResourceLoader();

	private BookContentResourceLoader() {}

	@Nullable
	@Override
	public InputStream loadJson(Book book, ResourceManager resourceManager, ResourceLocation file, @Nullable ResourceLocation fallback) {
		Patchouli.LOGGER.debug("Loading {}", file);
		try {
			if (resourceManager.hasResource(file)) {
				return resourceManager.getResource(file).getInputStream();
			} else if (fallback != null && resourceManager.hasResource(fallback)) {
				return resourceManager.getResource(fallback).getInputStream();
			} else {
				return null;
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
