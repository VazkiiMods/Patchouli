package vazkii.patchouli.client.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.Identifier;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.SerializationUtil;

public final class PersistentData {

	private static File saveFile;
	
	public static DataHolder data;
	
	public static void setup() {
		saveFile = new File("patchouli_data.json");
		load();
	}
	
	public static void load() {
		data = SerializationUtil.loadFromFile(saveFile, DataHolder.class, DataHolder::new);
	}
	
	public static void save() {
		SerializationUtil.saveToFile(SerializationUtil.PRETTY_GSON, saveFile, DataHolder.class, data);
	}
	
	public static final class DataHolder {
		
		public int bookGuiScale = 0;
		public boolean clickedVisualize = false;
		
		Map<String, BookData> bookData = new HashMap<>();
		
		public BookData getBookData(Book book) {
			String res = book.resourceLoc.toString();
			if(!bookData.containsKey(res))
				bookData.put(res, new BookData());
			
			return bookData.get(res);
		}
		
		public static final class BookData {
			
			public List<String> viewedEntries = new ArrayList<>();
			public List<Bookmark> bookmarks = new ArrayList<>();
			public List<String> history = new ArrayList<>();
			public List<String> completedManualQuests = new ArrayList<>();
			
			public static final class Bookmark {
				
				public String entry;
				public int page;
				
				public Bookmark(String entry, int page) {
					this.entry = entry;
					this.page = page;
				}
				
				public BookEntry getEntry(Book book) {
					Identifier res = new Identifier(entry);
					return book.contents.entries.get(res);
				}
				
			}
			
		}
		
	}
	
}
