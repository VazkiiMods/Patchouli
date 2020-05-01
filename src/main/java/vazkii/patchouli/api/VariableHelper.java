package vazkii.patchouli.api;

import com.google.gson.JsonElement;

import net.minecraft.util.LazyValue;

/**
 * API endpoint for serializing/deserializing arbitrary objects to/from JsonElements.
 */
public interface VariableHelper {

	LazyValue<VariableHelper> INSTANCE = new LazyValue<>(() -> {
		try {
			return (VariableHelper) Class.forName("vazkii.patchouli.client.book.template.variable.VariableHelperImpl").newInstance();
		} catch(ReflectiveOperationException e) {
			return new VariableHelper() {};
		}
	});

	static VariableHelper instance() {
		return INSTANCE.getValue();
	}


	default <T> IVariable createFromObject(T object) {
		return null;
	}

	default IVariable createFromJson(JsonElement elem) {
		return null;
	}

	default boolean hasSerializerFor(Class<?> clazz) {
		return false;
	}

	default <T> IVariableSerializer<T> serializerForClass(Class<?> clazz) {
		return null;
	}

	default	<T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		return instance();
	}
}
