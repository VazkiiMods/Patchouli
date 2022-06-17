package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Supplier;

public final class SerializationUtil {

	public static final Gson RAW_GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.registerTypeAdapter(IVariable.class, new IVariable.Serializer())
			.create();
	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

	private SerializationUtil() {}

	public static <T> T loadFromFile(File f, Class<? extends T> clazz, Supplier<T> baseCase) {
		return loadFromFile(RAW_GSON, f, clazz, baseCase);
	}

	public static <T> T loadFromFile(Gson gson, File f, Class<? extends T> clazz, Supplier<T> baseCase) {
		try {
			if (!f.exists()) {
				T t = baseCase.get();
				saveToFile(gson, f, clazz, t);
				return t;
			}

			FileInputStream in = new FileInputStream(f);
			return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), clazz);
		} catch (IOException e) {
			PatchouliAPI.LOGGER.error("Failed to load file", e);
			return null;
		}
	}

	public static <T> void saveToFile(File f, Class<? extends T> clazz, T obj) {
		saveToFile(RAW_GSON, f, clazz, obj);
	}

	public static <T> void saveToFile(Gson gson, File f, Class<? extends T> clazz, T obj) {
		String json = gson.toJson(obj, clazz);
		try {
			f.createNewFile();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
				writer.write(json);
			}
		} catch (IOException e) {
			PatchouliAPI.LOGGER.error("Failed to save file", e);
		}
	}

	public static ResourceLocation getAsResourceLocation(JsonObject object, String key, @Nullable ResourceLocation fallback) {
		if (object.has(key)) {
			return new ResourceLocation(GsonHelper.convertToString(object.get(key), key));
		} else {
			return fallback;
		}
	}

	@Nullable
	public static <T extends Enum<T>> T getAsEnum(JsonObject object, String key, Class<T> clz, @Nullable T fallback) {
		if (object.has(key)) {
			var str = GsonHelper.convertToString(object.get(key), key).toUpperCase(Locale.ROOT);
			return Enum.valueOf(clz, str);
		} else {
			return fallback;
		}
	}
}
