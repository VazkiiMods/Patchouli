package vazkii.patchouli.common.book;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.config.AppenderControlArraySet;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class BookRegistry extends SimplePreparableReloadListener<Map<ResourceLocation, ResourceLocation>> {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	private final Map<ResourceLocation, Book> books = new HashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.create();

	private boolean loaded = false;
	private final List<Runnable> callbacks = new ArrayList<>();

	private BookRegistry() {}

	public Optional<Book> getBook(final ResourceLocation location) {
		if (!isLoaded()) {
			return Optional.empty();
		}
		return Optional.ofNullable(books.get(location));
	}

	public Collection<Book> getBooks() {
		if (!isLoaded()) {
			return ImmutableSet.of();
		}
		return Collections.unmodifiableCollection(books.values());
	}

	public Set<ResourceLocation> getBookIds() {
		if (!isLoaded()) {
			return ImmutableSet.of();
		}
		return Collections.unmodifiableSet(books.keySet());
	}

	public boolean isBookLoaded(final ResourceLocation location) {
		return isLoaded() && books.containsKey(location);
	}

	@Nonnull
	@Override
	protected Map<ResourceLocation, ResourceLocation> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
		profiler.startTick();
		Map<ResourceLocation, ResourceLocation> bookRls = resourceManager.listResources(BOOKS_LOCATION, s -> s.endsWith("book.json"))
				.stream()
				.map(location -> {
					String fileStr = location.getPath();
					String relPath = fileStr.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
					String bookName = relPath.substring(0, relPath.indexOf("/"));
					return Pair.of(new ResourceLocation(location.getNamespace(), bookName), location);
				}).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		profiler.endTick();
		return bookRls;
	}

	@Override
	protected void apply(Map<ResourceLocation, ResourceLocation> books, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
		books.forEach((bookRl, location) -> {
			try(InputStream stream = resourceManager.getResource(location).getInputStream()) {
				loadBook(bookRl, stream);
			} catch (IOException e) {
				Patchouli.LOGGER.error("Failed to load book {} defined by mod {}, skipping",
						bookRl, bookRl.getNamespace(), e);
			}
		});
		runCallbacks();
	}

	private void runCallbacks() {
		for (Runnable callback : callbacks) {
			try {
					callback.run();
			} catch (Exception e) {
				Patchouli.LOGGER.error("Book reload callback errored!", e);
			}
		}
	}

	public void registerLoadedCallback(Runnable callback) {
		callbacks.add(callback);
	}

	public void loadBook(ResourceLocation res, InputStream stream) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = GSON.fromJson(reader, Book.class);
		book.build(res);
		books.put(res, book);
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadContents(ResourceManager resourceManager) {
		PatchouliConfig.reloadBuiltinFlags();
		for (Book book : getBooks()) {
			book.reloadContents(resourceManager);
		}
		ClientBookRegistry.INSTANCE.reloadLocks(false);
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
