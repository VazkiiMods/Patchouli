package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.common.base.Patchouli;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
			Patchouli.LOGGER.error("Failed to load file, using default", e);
			return baseCase.get();
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
			Patchouli.LOGGER.error("Failed to save file", e);
		}
	}
}
