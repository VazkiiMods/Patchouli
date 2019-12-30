package vazkii.patchouli.common.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.base.Patchouli;

public class BookFolderLoader {

	public static File loadDir;
	
	private static void setup() {
		loadDir = new File(BookRegistry.BOOKS_LOCATION);
		if(!loadDir.exists())
			loadDir.mkdir();
		else if(!loadDir.isDirectory())
			throw new RuntimeException(loadDir.getAbsolutePath() + " is a file, not a folder, aborting. Please delete this file or move it elsewhere if it has important contents.");
	}
	
	public static void findBooks() {
		if(loadDir == null)
			setup();

		ModContainer self = FabricLoader.getInstance().getModContainer(Patchouli.MOD_ID).get();
		File[] subdirs = loadDir.listFiles(File::isDirectory);
		for(File dir : subdirs) {
			File bookJson = new File(dir, "book.json");
			if(bookJson.exists()) {
				try {
					Identifier res = new Identifier(Patchouli.MOD_ID, dir.getName());
					FileInputStream stream = new FileInputStream(bookJson);
					BookRegistry.INSTANCE.loadBook(self, res, stream, true);
				} catch (IOException e) {
					Patchouli.LOGGER.error("Failed to load book.json", e);
				}
			}
		}
	}
	
}
