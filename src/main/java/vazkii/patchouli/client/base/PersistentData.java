package vazkii.patchouli.client.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

public final class PersistentData {

	private static File saveFile;
	
	public static DataHolder data;
	
	public static void setup(File dir) {
		saveFile = new File(dir, "patchouli_data.json");
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
		public List<String> viewedEntries = new ArrayList();
		public List<Bookmark> bookmarks = new ArrayList();
		public List<String> history = new ArrayList();
		
		public static final class Bookmark {
			
			public String entry;
			public int page;
			
			public Bookmark(String entry, int page) {
				this.entry = entry;
				this.page = page;
			}
			
			public BookEntry getEntry() {
				ResourceLocation res = new ResourceLocation(entry);
				return BookRegistry.INSTANCE.entries.get(res);
			}
			
		}
		
	}
	
}
