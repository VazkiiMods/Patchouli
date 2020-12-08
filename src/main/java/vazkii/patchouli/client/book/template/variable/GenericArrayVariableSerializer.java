package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import vazkii.patchouli.api.IVariableSerializer;

public class GenericArrayVariableSerializer<T> implements IVariableSerializer<T[]> {
	@SuppressWarnings("unchecked")
	public final T[] EMPTY = (T[]) new Object[0];
	private final IVariableSerializer<T> inner;

	public GenericArrayVariableSerializer(IVariableSerializer<T> inner) {
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
			return stacks.toArray(EMPTY);
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
