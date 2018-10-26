package vazkii.patchouli.common.book;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.client.base.ClientAdvancements;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final Map<ResourceLocation, Book> books = new HashMap();
	public Gson gson;
	
	private BookRegistry() { 
		gson = new GsonBuilder().create();
	}

	public void init() {
		List<ModContainer> mods = Loader.instance().getActiveModList();
		Map<Pair<ModContainer, ResourceLocation>, Reader> foundBooks = new HashMap();

		// TODO remove debugs
		mods.forEach((mod) -> {
			String id = mod.getModId();
			CraftingHelper.findFiles(mod, String.format("assets/%s/%s", id, BOOKS_LOCATION), (path) -> Files.exists(path),
			(path, file) -> {
				if(file.toString().endsWith("book.json")) {
					String fileStr = file.toString();
					System.out.println("fileStr: " + fileStr);
					String relPath = fileStr.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
					System.out.println("relPath: " + relPath);
					String bookName = relPath.substring(0, relPath.indexOf(File.separator));
					System.out.println("bookName: " + bookName);
					
					if(bookName.indexOf(File.separator) > -1) {
						(new IllegalArgumentException("Ignored book.json @ " + file)).printStackTrace();
						return true;
					}
					
					ResourceLocation bookId = new ResourceLocation(id, bookName);
					try {
						System.out.println("bookId: " + bookId);
						foundBooks.put(Pair.of(mod, bookId), Files.newBufferedReader(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				return true;
			}, false, true);
		});
		
		foundBooks.forEach((pair, reader) -> {
			Book book = gson.fromJson(reader, Book.class);
			books.put(pair.getRight(), book);
			
			book.build(pair.getLeft(), pair.getRight());
		});
	}

	@SideOnly(Side.CLIENT)
	public void reload() {
		BookRegistry.INSTANCE.books.values().forEach(Book::reloadContents);
		ClientAdvancements.updateLockStatus();
	}

}
