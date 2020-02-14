package vazkii.patchouli.client.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import net.minecraft.util.Identifier;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookFolderLoader;

public class ExternalBookContents extends BookContents {

	public ExternalBookContents(Book book) {
		super(book);
	}

	@Override
	protected void findFiles(String dir, List<Identifier> list) {
		File root = new File(BookFolderLoader.loadDir, book.id.getPath());
		File enUs = new File(root, DEFAULT_LANG);
		if(enUs.exists()) {
			File searchDir = new File(enUs, dir);
			if(searchDir.exists())
				crawl(searchDir, searchDir, list);
		}
	}
	
	private void crawl(File realRoot, File root, List<Identifier> list) {
		File[] files = root.listFiles();
		for(File f : files) {
			if(f.isDirectory())
				crawl(realRoot, f, list);
			else if(f.getName().endsWith(".json"))
				list.add(relativize(realRoot, f));
		}
	}
	
	private Identifier relativize(File root, File f) {
		String rootPath = root.getAbsolutePath();
		String filePath = f.getAbsolutePath().substring(rootPath.length() + 1);
		String cleanPath = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(filePath));
		
		return new Identifier(Patchouli.MOD_ID, cleanPath);
	}
	
	@Override
	protected InputStream loadJson(Identifier resloc, Identifier fallback) {
		String realPath = resloc.getPath().substring(BookFolderLoader.loadDir.getName().length());
		File targetFile = new File(BookFolderLoader.loadDir, realPath);
		
		if(targetFile.exists()) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(targetFile);
				return stream;
			} catch (IOException e) {
				Patchouli.LOGGER.catching(e);
			}
		}
		
		if(fallback != null) {
			Patchouli.LOGGER.warn("Failed to load " + resloc + ". Switching to fallback.");
			return loadJson(fallback, null);
		}
		
		return null;
	}
	
}
