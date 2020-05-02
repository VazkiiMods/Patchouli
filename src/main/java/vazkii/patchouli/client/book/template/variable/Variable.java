package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.Objects;

public class Variable implements IVariable {
	private final JsonElement value;

	public Variable(JsonElement elem) {
		value = Objects.requireNonNull(elem);
	}

	@Override
	public <T> T as(Class<T> clazz) {
		IVariableSerializer<T> serializer = VariableHelper.instance().<T>serializerForClass(clazz);

		if (serializer == null) {
			throw new IllegalArgumentException(String.format("Can't deserialize object of class %s from IVariable", clazz));
		}

		return serializer.fromJson(value);
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
