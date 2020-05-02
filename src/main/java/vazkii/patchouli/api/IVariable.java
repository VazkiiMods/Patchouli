package vazkii.patchouli.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

/**
 * A Patchouli derivation variable, represented internally as {@link JsonElement}.
 */
public interface IVariable {

	/**
	 * Interpret the JSON of this variable as a given Object class.
	 * Will return {@code null} if this type doesn't have a serializer registered;
	 * might return {@code null} or throw if the input is malformed.
	 * 
	 * @param  clazz the Class of the object you want to convert this to
	 * @return       this variable as an object of that class
	 */
	<T> T as(Class<T> clazz);

	/**
	 * Take a look at the underlying {@link JsonElement} for this variable.
	 * 
	 * @return the underlying JSON element for this Variable
	 */
	JsonElement unwrap();

	/**
	 * Get this IVariable as a String.
	 */
	default String asString() {
		return asString("");
	}

	/**
	 * Get this IVariable as a String, with a default value.
	 */
	default String asString(String def) {
		return unwrap().isJsonNull() ? def : unwrap().getAsString();
	}

	/**
	 * Get this IVariable as a Number.
	 */
	default Number asNumber() {
		return asNumber(0);
	}

	/**
	 * Get this IVariable as a Number, with a default value.
	 */
	default Number asNumber(Number def) {
		return unwrap().isJsonNull() ? def : unwrap().getAsNumber();
	}

	/**
	 * Get this IVariable as a boolean.
	 */
	default boolean asBoolean() {
		return asBoolean(false);
	}

	/**
	 * Get this IVariable as a boolean, with a default value.
	 */
	default boolean asBoolean(boolean def) {
		return unwrap().isJsonNull() ? def : (!unwrap().getAsString().equals("false") && unwrap().getAsBoolean());
	}

	/**
	 * Convenience method to create an IVariable from {@link VariableHelper#createFromObject}.
	 */
	static <T> IVariable from(T object) {
		return object != null ? VariableHelper.instance().createFromObject(object) : empty();
	}

	/**
	 * Convenience method to create an IVariable from {@link VariableHelper#createFromJson}.
	 */
	static IVariable wrap(JsonElement elem) {
		return elem != null ? VariableHelper.instance().createFromJson(elem) : empty();
	}

	static IVariable wrap(Number n) {
		return n != null ? wrap(new JsonPrimitive(n)) : empty();
	}

	static IVariable wrap(Boolean b) {
		return b != null ? wrap(new JsonPrimitive(b)) : empty();
	}

	static IVariable wrap(String s) {
		return s != null ? wrap(new JsonPrimitive(s)) : empty();
	}

	static IVariable empty() {
		return wrap(JsonNull.INSTANCE);
	}
}
