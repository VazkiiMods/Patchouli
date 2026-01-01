package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import vazkii.patchouli.api.IVariable;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class SerializationUtil {

	public static final IVariable.Serializer VARIABLE_SERIALIZER = new IVariable.Serializer();
	public static final Gson RAW_GSON = new GsonBuilder()
			.registerTypeAdapter(
					Identifier.class, (JsonDeserializer<Identifier>) (json, typeOfT, context) -> Identifier.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
			)
			.registerTypeAdapter(IVariable.class, VARIABLE_SERIALIZER)
			.create();
	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

	private SerializationUtil() {}

	public static Identifier getAsIdentifier(JsonObject object, String key, @Nullable Identifier fallback) {
		if (object.has(key)) {
			return Identifier.tryParse(GsonHelper.convertToString(object.get(key), key));
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
