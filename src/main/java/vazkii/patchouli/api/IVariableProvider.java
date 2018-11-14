package vazkii.patchouli.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A provider of variables to a template. Usually from json.
 */
@SideOnly(Side.CLIENT)
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
