package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import vazkii.patchouli.api.IVariableSerializer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GenericArrayVariableSerializer<T> implements IVariableSerializer<T[]> {
	protected final T[] empty;
	private final IVariableSerializer<T> inner;

	@SuppressWarnings("unchecked")
	public GenericArrayVariableSerializer(IVariableSerializer<T> inner, Class<T> type) {
		this.empty = (T[]) Array.newInstance(type, 0);
		this.inner = inner;
	}

	@Override
	public T[] fromJson(JsonElement json) {
		if (json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			List<T> stacks = new ArrayList<>();
			for (JsonElement e : array) {
				stacks.add(inner.fromJson(e));
			}
			return stacks.toArray(empty);
		}
		throw new IllegalArgumentException("Can't create an array of objects from a non-array JSON!");
	}

	@Override
	public JsonArray toJson(T[] array) {
		JsonArray result = new JsonArray();
		for (T elem : array) {
			result.add(inner.toJson(elem));
		}
		return result;
	}

}
