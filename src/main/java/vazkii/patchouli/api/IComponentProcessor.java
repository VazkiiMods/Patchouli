package vazkii.patchouli.api;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Implement this on a class designed to process a template and the variables bound 
 * to the inside. This doesn't have to be registered anywhere, but any class implementing
 * this needs either no explicit constructor, or an implicit public no parameter constructor.
 * <br><br>
 * Instances of this class are created once for every usage of the template it's meant to 
 * process, so you can save data on a per-template basis.
 */
@SideOnly(Side.CLIENT)
public interface IComponentProcessor {

	/**
	 * Called as the template is being built with the variables passed into it. Use
	 * this to set up any data that will be used later. Any exceptions thrown here
	 * will be caught by the book loader, and graciously displayed in a user friendly
	 * way, so feel free to throw on any unrecoverable states.
	 * @param variables
	 */
	public void setup(IVariableProvider<String> variables);

	/**
	 * Processes a variable. You should return what you think the result should be, or
	 * null if you don't want to process it. If you need to pass in a serialized
	 * ItemStack, look at the ItemStack serialization methods in the main API class.
	 * <br><br>
	 * Note that this is also called for any right-side values of the using block of
	 * any included templates. It is not called for the variables used inside said
	 * templates. 
	 */
	public String process(String key);

	/**
	 * Called when a book GUI containing this page is showed (by guiInit).
	 */
	public default void refresh(GuiScreen parent, int left, int top) {
		// NO-OP
	}

	/**
	 * Returns whether an element is allowed to render based on its group. This method
	 * isn't called by elements with no group or with an empty group.
	 * <br><br>
	 * In the case of included templates existing, groups are automatically assigned
	 * to them, and this will be called for every element inside them with said groups.
	 * Eg. Including another template as "sub", which has elements with no group, and with
	 * groups "group1" and "group2" will have this be called for "sub", "sub.group1", and
	 * "sub.group2".
	 */
	public default boolean allowRender(String group) {
		return true;
	}

}	
