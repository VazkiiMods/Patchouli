package vazkii.patchouli.client.book;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public interface BookContentLoader {
	void findFiles(Book book, String dir, List<ResourceLocation> list);

	@Nullable
	InputStream loadJson(Book book, ResourceLocation resloc, @Nullable ResourceLocation fallback);
}
