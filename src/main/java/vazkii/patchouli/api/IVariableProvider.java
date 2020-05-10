package vazkii.patchouli.api;

/**
 * A provider of variables to a template. Usually from json.
 */
public interface IVariableProvider<T> {

	/**
	 * Gets the value assigned to the variable passed in.
	 * May throw an exception if it doesn't exist.
	 */
	T get(String key);

	/**
	 * Returns if a variable exists or not.
	 */
	boolean has(String key);

}
