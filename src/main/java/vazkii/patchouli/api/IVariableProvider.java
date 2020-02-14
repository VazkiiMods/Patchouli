package vazkii.patchouli.api;

/**
 * A provider of variables to a template. Usually from json.
 */
public interface IVariableProvider<T> {

	/**
	 * Gets the value assigned to the variable passed in.
	 * May throw an exception if it doesn't exist. 
	 */
	public T get(String key);
	
	/**
	 * Returns if a variable exists or not.
	 */
	public boolean has(String key);

}
