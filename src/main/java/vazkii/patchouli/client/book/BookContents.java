package vazkii.patchouli.client.book;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookLanding;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

public class BookContents {

	private static final String[] ORDINAL_SUFFIXES = new String[]{ "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	private static final String DEFAULT_LANG = "en_us";

	public final Book book;

	public Map<ResourceLocation, BookCategory> categories = new HashMap();
	public Map<ResourceLocation, BookEntry> entries = new HashMap();
	public Map<StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap();
	private boolean errored = false;

	public Stack<GuiBook> guiStack = new Stack();
	public GuiBook currentGui;

	public BookContents(Book book) {
		this.book = book;
	}

	public boolean isErrored() {
		return errored;
	}

	public Pair<BookEntry, Integer> getEntryForStack(ItemStack stack) {
		return recipeMappings.get(ItemStackUtil.wrapStack(stack));
	}

	public GuiBook getCurrentGui() {
		if(currentGui == null)
			currentGui = new GuiBookLanding(book);

		return currentGui;
	}

	public void openLexiconGui(GuiBook gui, boolean push) {
		if(gui.canBeOpened()) {
			Minecraft mc = Minecraft.getMinecraft();
			if(push && mc.currentScreen instanceof GuiBook && gui != mc.currentScreen)
				guiStack.push((GuiBook) mc.currentScreen);

			mc.displayGuiScreen(gui);
			gui.onFirstOpened();
		}
	}

	public String getSubtitle() {
		String editionStr = "";

		try {
			int ver = Integer.parseInt(book.version);
			if(ver == 0)
				return I18n.translateToLocal(book.subtitle);

			editionStr = numberToOrdinal(ver); 
		} catch(NumberFormatException e) {
			editionStr = I18n.translateToLocal("patchouli.gui.lexicon.dev_edition");
		}

		return I18n.translateToLocalFormatted("patchouli.gui.lexicon.edition_str", editionStr);
	}

	public void reload(boolean isOverride) {
		errored = false;

		if(!isOverride) {
			currentGui = null;
			guiStack.clear();
			categories.clear();
			entries.clear();
			recipeMappings.clear();
		}

		List<ResourceLocation> foundCategories = new ArrayList();
		List<ResourceLocation> foundEntries = new ArrayList();
		List<ModContainer> mods = Loader.instance().getActiveModList();

		try { 
			ModContainer mod = book.owner;
			String id = book.getModNamespace();
			String loc = BookRegistry.BOOKS_LOCATION;
			String bookName = book.resourceLoc.getResourcePath();

			CraftingHelper.findFiles(mod, String.format("assets/%s/%s/%s/%s/categories", id, loc, bookName, DEFAULT_LANG), null, pred(id, foundCategories));
			CraftingHelper.findFiles(mod, String.format("assets/%s/%s/%s/%s/entries", id, loc, bookName, DEFAULT_LANG), null, pred(id, foundEntries));

			foundCategories.forEach(c -> loadCategory(c, new ResourceLocation(c.getResourceDomain(),
					String.format("%s/%s/%s/categories/%s.json", loc, bookName, DEFAULT_LANG, c.getResourcePath())), book));
			foundEntries.forEach(e -> loadEntry(e, new ResourceLocation(e.getResourceDomain(),
					String.format("%s/%s/%s/entries/%s.json", loc, bookName, DEFAULT_LANG, e.getResourcePath())), book));

			entries.forEach((res, entry) -> {
				try {
					entry.build(res);
				} catch(Exception e) {
					throw new RuntimeException("Error while loading entry " + res, e);
				}
			});

			categories.forEach((res, category) -> {
				try {
					category.build(res);
				} catch(Exception e) {
					throw new RuntimeException("Error while loading category " + res, e);
				}
			});
		} catch (Exception e) {
			errored = true;
			e.printStackTrace();
		}
	}

	private BiFunction<Path, Path, Boolean> pred(String modId, List<ResourceLocation> list) {
		return (root, file) -> {
			Path rel = root.relativize(file);
			String relName = rel.toString();
			if (relName.toString().endsWith(".json")) {
				relName = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(relName));
				ResourceLocation res = new ResourceLocation(modId, relName);
				list.add(res);
			}

			return true;
		};
	}

	private void loadCategory(ResourceLocation key, ResourceLocation res, Book book) {
		InputStream stream = loadLocalizedJson(res);
		if (stream == null)
			throw new IllegalArgumentException(res + " does not exist.");

		BookCategory category = ClientBookRegistry.INSTANCE.gson.fromJson(new InputStreamReader(stream), BookCategory.class);
		if (category == null)
			throw new IllegalArgumentException(res + " does not exist.");

		category.setBook(book);
		if (category.canAdd())
			categories.put(key, category);
	}

	private void loadEntry(ResourceLocation key, ResourceLocation res, Book book) {
		InputStream stream = loadLocalizedJson(res);
		if (stream == null)
			throw new IllegalArgumentException(res + " does not exist.");

		BookEntry entry = ClientBookRegistry.INSTANCE.gson.fromJson(new InputStreamReader(stream), BookEntry.class);
		if (entry == null)
			throw new IllegalArgumentException(res + " does not exist.");

		entry.setBook(book);
		if (entry.canAdd()) {
			BookCategory category = entry.getCategory();
			if (category != null)
				category.addEntry(entry);
			else
				new RuntimeException("Entry " + key + " does not have a valid category.").printStackTrace();

			entries.put(key, entry);
		}
	}

	private InputStream loadLocalizedJson(ResourceLocation res) {
		ResourceLocation localized = new ResourceLocation(res.getResourceDomain(),
				res.getResourcePath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		return loadJson(localized, null);
	}

	private InputStream loadJson(ResourceLocation resloc, ResourceLocation fallback) {
		IResource res = null;
		try {
			res = Minecraft.getMinecraft().getResourceManager().getResource(resloc);
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (res == null && fallback != null) {
			new RuntimeException("Patchouli failed to load " + resloc + ". Switching to fallback.").printStackTrace();
			return loadJson(fallback, null);
		}

		return res.getInputStream();
	}

	private static String numberToOrdinal(int i) {
		return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + ORDINAL_SUFFIXES[i % 10];
	}

}
