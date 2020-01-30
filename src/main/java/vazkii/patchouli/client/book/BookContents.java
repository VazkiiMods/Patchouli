package vazkii.patchouli.client.book;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookLanding;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

public class BookContents extends AbstractReadStateHolder {

	private static final String[] ORDINAL_SUFFIXES = new String[]{ "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	protected static final String DEFAULT_LANG = "en_us";
	
	public static final HashMap<ResourceLocation, Supplier<BookTemplate>> addonTemplates = new HashMap<>();

	public final Book book;

	public Map<ResourceLocation, BookCategory> categories = new HashMap<>();
	public Map<ResourceLocation, BookEntry> entries = new HashMap<>();
	public Map<ResourceLocation, Supplier<BookTemplate>> templates = new HashMap<>();
	public Map<StackWrapper, Pair<BookEntry, Integer>> recipeMappings = new HashMap<>();
	private boolean errored = false;
	private Exception exception = null;

	public Deque<GuiBook> guiStack = new ArrayDeque<>();
	public GuiBook currentGui;
	
	public BookIcon indexIcon;

	public BookContents(Book book) {
		this.book = book;
	}

	public boolean isErrored() {
		return errored;
	}
	
	public Exception getException() {
		return exception;
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
			Minecraft mc = Minecraft.getInstance();
			if(push && mc.currentScreen instanceof GuiBook && gui != mc.currentScreen)
				guiStack.push((GuiBook) mc.currentScreen);

			mc.displayGuiScreen(gui);
			gui.onFirstOpened();
		}
	}

	public String getSubtitle() {
		String editionStr;

		try {
			int ver = Integer.parseInt(book.version);
			if(ver == 0)
				return I18n.format(book.subtitle);

			editionStr = numberToOrdinal(ver); 
		} catch(NumberFormatException e) {
			editionStr = I18n.format("patchouli.gui.lexicon.dev_edition");
		}

		return I18n.format("patchouli.gui.lexicon.edition_str", editionStr);
	}

	public void reload(boolean isOverride) {
		errored = false;

		if(!isOverride) {
			currentGui = null;
			guiStack.clear();
			categories.clear();
			entries.clear();
			templates.clear();
			recipeMappings.clear();
			
			templates.putAll(addonTemplates);
			
			if(book.indexIconRaw == null || book.indexIconRaw.isEmpty())
				indexIcon = new BookIcon(book.getBookItem());
			else indexIcon = BookIcon.from(book.indexIconRaw);
		}

		List<ResourceLocation> foundCategories = new ArrayList<>();
		List<ResourceLocation> foundEntries = new ArrayList<>();
		List<ResourceLocation> foundTemplates = new ArrayList<>();

		try { 
			String bookName = book.resourceLoc.getPath();

			findFiles("categories", foundCategories);
			findFiles("entries", foundEntries);
			findFiles("templates", foundTemplates);
			
			foundCategories.forEach(c -> loadCategory(c, new ResourceLocation(c.getNamespace(),
					String.format("%s/%s/%s/categories/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, c.getPath())), book));
			foundEntries.forEach(e -> loadEntry(e, new ResourceLocation(e.getNamespace(),
					String.format("%s/%s/%s/entries/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, e.getPath())), book));
			foundTemplates.forEach(e -> loadTemplate(e, new ResourceLocation(e.getNamespace(),
					String.format("%s/%s/%s/templates/%s.json", BookRegistry.BOOKS_LOCATION, bookName, DEFAULT_LANG, e.getPath())), book));

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
			exception = e;
			errored = true;
			Patchouli.LOGGER.error("Error while loading contents for book {}", book.resourceLoc, e);
		}
	}

	protected void findFiles(String dir, List<ResourceLocation> list) {
		IModInfo mod = book.owner;
		if(mod instanceof ModInfo) {
			String id = mod.getModId();
			BookRegistry.findFiles((ModInfo) mod, String.format("data/%s/%s/%s/%s/%s", id, BookRegistry.BOOKS_LOCATION, book.resourceLoc.getPath(), DEFAULT_LANG, dir), null, pred(id, list), false, false);
		}
	}
	
	private BiFunction<Path, Path, Boolean> pred(String modId, List<ResourceLocation> list) {
		return (root, file) -> {
			Path rel = root.relativize(file);
			String relName = rel.toString();
			if(relName.endsWith(".json")) {
				relName = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(relName));
				ResourceLocation res = new ResourceLocation(modId, relName);
				list.add(res);
			}

			return true;
		};
	}

	private void loadCategory(ResourceLocation key, ResourceLocation res, Book book) {
		try (Reader stream = loadLocalizedJson(res)) {
			BookCategory category = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookCategory.class);
			if (category == null)
				throw new IllegalArgumentException(res + " does not exist.");

			category.setBook(book);
			if (category.canAdd())
				categories.put(key, category);
		} catch (IOException ex) {
			Patchouli.LOGGER.error("Exception reading category {}", res);
		}
	}

	private void loadEntry(ResourceLocation key, ResourceLocation res, Book book) {
		try (Reader stream = loadLocalizedJson(res)) {
			BookEntry entry = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookEntry.class);
			if (entry == null)
				throw new IllegalArgumentException(res + " does not exist.");

			entry.setBook(book);
			if (entry.canAdd()) {
				BookCategory category = entry.getCategory();
				if (category != null)
					category.addEntry(entry);
				else
					Patchouli.LOGGER.error("Entry {} in {} does not have a valid category.", key, res);

				entries.put(key, entry);
			}
		} catch (IOException ex) {
			Patchouli.LOGGER.error("Exception reading entry {}", res);
		}
	}
	
	private void loadTemplate(ResourceLocation key, ResourceLocation res, Book book) {
		Supplier<BookTemplate> supplier = () -> {
			try (Reader stream = loadLocalizedJson(res)) {
				return ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookTemplate.class);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};
		
		// test supplier
		BookTemplate template = supplier.get();
		if(template == null)
			throw new IllegalArgumentException(res + " could not be instantiated by the supplier.");
		
		templates.put(key, supplier);
	}

	private Reader loadLocalizedJson(ResourceLocation res) {
		ResourceLocation localized = new ResourceLocation(res.getNamespace(),
				res.getPath().replaceAll(DEFAULT_LANG, ClientBookRegistry.INSTANCE.currentLang));

		InputStream input = loadJson(localized, res);
		if (input == null)
			throw new IllegalArgumentException(res + " does not exist.");

		return new InputStreamReader(new BufferedInputStream(input), StandardCharsets.UTF_8);
	}

	protected InputStream loadJson(ResourceLocation resloc, ResourceLocation fallback) {
		String path = "/data/" + resloc.getNamespace() + "/" + resloc.getPath();
		Patchouli.LOGGER.debug("Loading {}", path);
		
		InputStream stream = book.ownerClass.getResourceAsStream(path);
		if(stream != null)
			return stream;

		if(fallback != null) {
			Patchouli.LOGGER.warn("Failed to load " + resloc + ". Switching to fallback.");
			return loadJson(fallback, null);
		}

		return null;
	}

	private static String numberToOrdinal(int i) {
		return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + ORDINAL_SUFFIXES[i % 10];
	}

	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> stream = categories.values().stream().filter(BookCategory::isRootCategory).map(BookCategory::getReadState);
		return mostImportantState(stream);
	}

}
