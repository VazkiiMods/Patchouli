package vazkii.patchouli.client.book.template.variable;

import java.util.Objects;

import com.google.gson.JsonElement;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.VariableHelper;

public class Variable implements IVariable {
	private final JsonElement value;

	public Variable(JsonElement elem) {
		value = Objects.requireNonNull(elem);
	}

	@Override
	public <T> T as(Class<T> clazz) {
		if (!VariableHelper.instance().hasSerializerFor(clazz)) {
			throw new IllegalArgumentException(String.format("Can't deserialize object of type %s from IVariable", clazz));
		}

		return VariableHelper.instance().<T>serializerForClass(clazz).fromJson(value);
	}

	@Override
	public JsonElement unwrap() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
