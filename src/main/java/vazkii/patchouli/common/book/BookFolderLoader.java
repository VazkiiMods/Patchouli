package vazkii.patchouli.common.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
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
		if (subdirs == null) {
			Patchouli.LOGGER.warn("Failed to list external books in {}, not loading external books",
					loadDir.getAbsolutePath());
			return;
		}

		for(File dir : subdirs) {
			Identifier res;
			try {
				res = new Identifier(Patchouli.MOD_ID, dir.getName());
			} catch (InvalidIdentifierException ex) {
				Patchouli.LOGGER.error("Invalid external book folder name {}, skipping", dir.getName(), ex);
				continue;
			}

			File bookJson = new File(dir, "book.json");
			try (FileInputStream stream = new FileInputStream(bookJson)) {
				BookRegistry.INSTANCE.loadBook(self, res, stream, true);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
			}
		}
	}
	
}
