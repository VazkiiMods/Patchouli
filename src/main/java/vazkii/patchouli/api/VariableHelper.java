package vazkii.patchouli.api;

import com.google.gson.JsonElement;

import net.minecraft.util.LazyValue;

import javax.annotation.Nullable;

/**
 * API endpoint for serializing/deserializing arbitrary objects to/from JsonElements.
 */
public interface VariableHelper {

	LazyValue<VariableHelper> INSTANCE = new LazyValue<>(() -> {
		try {
			return (VariableHelper) Class.forName("vazkii.patchouli.client.book.template.variable.VariableHelperImpl").newInstance();
		} catch (ReflectiveOperationException e) {
			return new VariableHelper() {};
		}
	});

	/**
	 * Get the singleton instance of this VariableHelper.
	 */
	static VariableHelper instance() {
		return INSTANCE.getValue();
	}

	/**
	 * Create an {@link IVariable} from a given object.
	 */
	default <T> IVariable createFromObject(T object) {
		return null;
	}

	/**
	 * Create an {@link IVariable} backed by the given {@link JsonElement}.
	 */
	default IVariable createFromJson(JsonElement elem) {
		return null;
	}

	/**
	 * Get an {@link IVariableSerializer} for the given class.
	 * If we don't know how to deserialize this class, return {@code null}.
	 */
	@Nullable
	default <T> IVariableSerializer<T> serializerForClass(Class<?> clazz) {
		return null;
	}

	/**
	 * Register an {@link IVariableSerializer} that can deserialize an object of the given class.
	 * Note that a sane serializer should probably cope with subclasses gracefully.
	 */
	default <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		return instance();
	}
}
