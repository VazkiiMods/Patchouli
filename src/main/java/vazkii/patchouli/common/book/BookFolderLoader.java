package vazkii.patchouli.common.book;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import vazkii.patchouli.common.base.Patchouli;

import java.io.File;
import java.io.FileInputStream;

public class BookFolderLoader {

	public static File loadDir;

	private static void setup() {
		loadDir = new File(BookRegistry.BOOKS_LOCATION);
		if (!loadDir.exists()) {
			loadDir.mkdir();
		} else if (!loadDir.isDirectory()) {
			throw new RuntimeException(loadDir.getAbsolutePath() + " is a file, not a folder, aborting. Please delete this file or move it elsewhere if it has important contents.");
		}
	}

	public static void findBooks() {
		if (loadDir == null) {
			setup();
		}

		ModContainer self = ModList.get().getModContainerById(Patchouli.MOD_ID).get();
		File[] subdirs = loadDir.listFiles(File::isDirectory);
		if (subdirs == null) {
			Patchouli.LOGGER.warn("Failed to list external books in {}, not loading external books",
					loadDir.getAbsolutePath());
			return;
		}

		for (File dir : subdirs) {
			ResourceLocation res;
			try {
				res = new ResourceLocation(Patchouli.MOD_ID, dir.getName());
			} catch (ResourceLocationException ex) {
				Patchouli.LOGGER.error("Invalid external book folder name {}, skipping", dir.getName(), ex);
				continue;
			}

			File bookJson = new File(dir, "book.json");
			try (FileInputStream stream = new FileInputStream(bookJson)) {
				BookRegistry.INSTANCE.loadBook(self.getModInfo(), res, stream, true);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
			}
		}
	}

}
