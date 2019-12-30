package vazkii.patchouli.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A provider of variables to a template. Usually from json.
 */
@Environment(EnvType.CLIENT)
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
