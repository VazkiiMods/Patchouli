package vazkii.patchouli.client.base;

import com.google.gson.annotations.SerializedName;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.SerializationUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		Map<String, PersistentData.BookData> bookData = new HashMap<>();

		public PersistentData.BookData getBookData(Book book) {
			String res = book.id.toString();
			if (!bookData.containsKey(res)) {
				bookData.put(res, new PersistentData.BookData());
			}

			return bookData.get(res);
		}
	}

	public static final class Bookmark {
		public String entry;
		// Serialized as page for legacy reasons
		@SerializedName("page") public int spread;

		public Bookmark(String entry, int spread) {
			this.entry = entry;
			this.spread = spread;
		}

		public BookEntry getEntry(Book book) {
			ResourceLocation res = new ResourceLocation(entry);
			return book.getContents().entries.get(res);
		}
	}

	public static final class BookData {
		public List<String> viewedEntries = new ArrayList<>();
		public List<Bookmark> bookmarks = new ArrayList<>();
		public List<String> history = new ArrayList<>();
		public List<String> completedManualQuests = new ArrayList<>();
	}
}
