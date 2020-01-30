package vazkii.patchouli.common.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
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
		
		IModInfo mod = ModLoadingContext.get().getActiveContainer().getModInfo();
		File[] subdirs = loadDir.listFiles(File::isDirectory);
		if (subdirs == null) {
			Patchouli.LOGGER.warn("Failed to list external books in {}, not loading external books",
					loadDir.getAbsolutePath());
			return;
		}

		for(File dir : subdirs) {
			ResourceLocation res;
			try {
				res = new ResourceLocation(Patchouli.MOD_ID, dir.getName());
			} catch (ResourceLocationException ex) {
				Patchouli.LOGGER.error("Invalid external book folder name {}, skipping", dir.getName(), ex);
				continue;
			}

			File bookJson = new File(dir, "book.json");
			try (FileInputStream stream = new FileInputStream(bookJson)) {
				BookRegistry.INSTANCE.loadBook(mod, Patchouli.class, res, stream, true);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
			}
		}
	}
	
}
