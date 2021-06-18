package vazkii.patchouli.client.book;

import net.minecraft.util.Identifier;

import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public interface BookContentLoader {
	void findFiles(Book book, String dir, List<Identifier> list);

	@Nullable
	InputStream loadJson(Book book, Identifier resloc, @Nullable Identifier fallback);
}
