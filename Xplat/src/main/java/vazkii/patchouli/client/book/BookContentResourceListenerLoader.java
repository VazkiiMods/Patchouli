package vazkii.patchouli.client.book;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BookContentLoader similar to {@link BookContentResourceDirectLoader}, but it
 * pre-caches the JSONs at during resource load to avoid I/O during book reloads.
 */
public class BookContentResourceListenerLoader extends SimpleJsonResourceReloadListener<JsonElement>
		implements BookContentLoader {
	public static final BookContentResourceListenerLoader INSTANCE = new BookContentResourceListenerLoader();
	private static final Pattern ID_READER = Pattern.compile(
			"(?<bookId>[a-z0-9_.-]+)" +
					"/(?<lang>[a-z0-9_.-]+)" +
					"/(?<folder>[a-z0-9_.-]+)" +
					"/(?<entryId>[a-z0-9/._-]+)");

	// book id -> (entry id -> entry json)
	private Map<ResourceLocation, Map<ResourceLocation, JsonElement>> data;

	private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
			d -> d.convert(JsonOps.INSTANCE).getValue(),
			e -> new Dynamic<>(JsonOps.INSTANCE, e)
	);

	private BookContentResourceListenerLoader() {
		super(JSON_ELEMENT_CODEC, FileToIdConverter.json(BookRegistry.BOOKS_LOCATION));
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		Map<ResourceLocation, Map<ResourceLocation, JsonElement>> data = new HashMap<>();
		for (var entry : map.entrySet()) {
			// namespace:book_name/en_us/entries/entry
			var key = entry.getKey();
			var matcher = ID_READER.matcher(key.getPath());
			if (!matcher.matches()) {
				PatchouliAPI.LOGGER.trace("Ignored file {}", key);
				continue;
			}
			var bookId = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), matcher.group("bookId"));

			data.computeIfAbsent(bookId, id -> new HashMap<>()).put(entry.getKey(), entry.getValue());
		}

		int count = data.values().stream().mapToInt(Map::size).sum();
		PatchouliAPI.LOGGER.info("{} preloaded {} jsons", getClass().getSimpleName(), count);
		this.data = data;
	}

	@Override
	public void findFiles(Book book, String dir, List<ResourceLocation> list) {
		if (this.data == null) {
			BookContentResourceDirectLoader.INSTANCE.findFiles(book, dir, list);
			return;
		}
		var stopwatch = Stopwatch.createStarted();

		var map = data.get(book.id);
		if (map == null) {
			return;
		}
		for (ResourceLocation id : map.keySet()) {
			var matcher = ID_READER.matcher(id.getPath());
			if (!matcher.matches()) {
				continue;
			}
			if (dir.equals(matcher.group("folder"))
					&& BookContentsBuilder.DEFAULT_LANG.equals(matcher.group("lang"))) {
				list.add(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), matcher.group("entryId")));
			}
		}

		PatchouliAPI.LOGGER.info("{}: Files found in {}", getClass().getSimpleName(), stopwatch.stop());
	}

	@Nullable
	@Override
	public LoadResult loadJson(Book book, ResourceLocation file) {
		if (this.data == null) {
			return BookContentResourceDirectLoader.INSTANCE.loadJson(book, file);
		}
		PatchouliAPI.LOGGER.trace("Loading {}", file);
		var map = data.get(book.id);
		if (map == null) {
			return null;
		}
		String path = file.getPath();
		// Drop patchouli_books/ and json suffix
		String relativizedPath = path.substring(0, path.length() - 5).split("/", 2)[1];

		JsonElement json = map.get(ResourceLocation.fromNamespaceAndPath(file.getNamespace(), relativizedPath));
		if (json != null) {
			return new LoadResult(
					json,
					// todo implement this
					null
			);
		}

		return null;
	}
}