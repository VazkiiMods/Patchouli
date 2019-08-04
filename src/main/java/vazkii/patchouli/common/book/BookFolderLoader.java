package vazkii.patchouli.common.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.minecraft.util.ResourceLocation;
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
		for(File dir : subdirs) {
			File bookJson = new File(dir, "book.json");
			if(bookJson.exists()) {
				try {
					ResourceLocation res = new ResourceLocation(Patchouli.MOD_ID, dir.getName());
					FileInputStream stream = new FileInputStream(bookJson);
					BookRegistry.INSTANCE.loadBook(mod, res, stream, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
