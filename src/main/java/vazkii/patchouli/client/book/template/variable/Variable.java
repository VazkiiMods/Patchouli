package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;

import java.util.Objects;

public class Variable implements IVariable {
	private final JsonElement value;

	@Nullable private final Class<?> sourceClass;

	public Variable(JsonElement elem, Class<?> c) {
		value = Objects.requireNonNull(elem);
		sourceClass = c;
	}

	@Override
	public <T> T as(Class<T> clazz) {
		if (sourceClass != null && !sourceClass.isAssignableFrom(clazz)) {
			Patchouli.LOGGER.warn("You're trying to deserialize an object of type %s from one of type %s. This is likely not what you want!", clazz, sourceClass);
		}

		IVariableSerializer<T> serializer = VariableHelper.instance().<T>serializerForClass(clazz);

		if (serializer == null) {
			throw new IllegalArgumentException(String.format("Can't deserialize object of class %s from IVariable", clazz));
		}

		return serializer.fromJson(value);
	}

	@Override
	public JsonElement unwrap() {
		if (sourceClass != null) {
			Patchouli.LOGGER.warn("You're trying to unwrap an object serialized as type %s directly as JSON. This is likely not what you want!", sourceClass);
		}

		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
