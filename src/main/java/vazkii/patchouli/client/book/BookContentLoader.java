package vazkii.patchouli.client.book;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.ResourceManager;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public interface BookContentLoader {

	@Nullable
	InputStream loadJson(Book book, ResourceManager resourceManager, ResourceLocation file, @Nullable ResourceLocation fallback);
}
