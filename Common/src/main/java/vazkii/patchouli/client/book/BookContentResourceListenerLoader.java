package vazkii.patchouli.client.book;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import org.apache.commons.io.IOUtils;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BookContentResourceListenerLoader extends SimpleJsonResourceReloadListener
		implements BookContentLoader {
	public static final BookContentResourceListenerLoader INSTANCE = new BookContentResourceListenerLoader();
	private static final Pattern ID_READER = Pattern.compile(
			"(?<bookId>[a-z0-9_.-]+)" +
					"/(?<lang>[a-z0-9_.-]+)" +
					"/(?<folder>[a-z0-9_.-]+)" +
					"/(?<entryId>[a-z0-9_.-/]+)");

	// book id -> (entry id -> entry json)
	private Map<ResourceLocation, Map<ResourceLocation, JsonElement>> data;

	private BookContentResourceListenerLoader() {
		super(BookRegistry.GSON, "patchouli_books");
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
			var bookId = new ResourceLocation(key.getNamespace(), matcher.group("bookId"));

			data.computeIfAbsent(bookId, id -> new HashMap<>()).put(entry.getKey(), entry.getValue());
		}

		this.data = data;
	}

	@Override
	public void findFiles(Book book, String dir, List<ResourceLocation> list) {
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
				list.add(new ResourceLocation(id.getNamespace(), matcher.group("entryId")));
			}
		}

		PatchouliAPI.LOGGER.info("Files found in {}", stopwatch.stop());
	}

	@Nullable
	@Override
	public InputStream loadJson(Book book, ResourceLocation location, @Nullable ResourceLocation fallback) {
		PatchouliAPI.LOGGER.trace("Loading {} with fallback {}", location, fallback);
		var map = data.get(book.id);
		if (map == null) {
			return null;
		}
		String path = location.getPath();

		JsonElement json = map.get(new ResourceLocation(location.getNamespace(), path.substring(0, path.length() - 5).split("/", 2)[1]));
		if (json == null && fallback != null) {
			path = fallback.getPath();
			json = map.get(new ResourceLocation(fallback.getNamespace(), path.substring(0, path.length() - 5).split("/", 2)[1]));
		}
		if (json == null) {
			return null;
		}
		return IOUtils.toInputStream(BookRegistry.GSON.toJson(json), StandardCharsets.UTF_8);
	}
}
