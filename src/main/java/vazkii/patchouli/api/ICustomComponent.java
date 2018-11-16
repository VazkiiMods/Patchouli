package vazkii.patchouli.api;

import net.minecraft.client.gui.GuiScreen;

/**
 * An interface for API level custom components for templates.
 * <br><br>
 * <b>WARNING:</b> Any fields you declare in instances of this will be deserialized from
 * gson. Any fields that you don't want to be read from the json should have
 * the transient keyword. 
 * <br><br>
 * Non-transient String type fields you declare in instances of this may have the
 * {@link VariableHolder} annotation associated to them, and as such, they'll load variables
 * the same way any built-in template would. Any fields with a @VariableHolder must be public.
 */
public interface ICustomComponent {

	/**
	 * Called when this component is built. Take the chance to read variables and set
	 * any local positions here.
	 */
	public void build(int componentX, int componentY, int pageNum);

	/**
	 * Called every render tick. No special transformations are applied, so you're responsible
	 * for putting everything in the right place.
	 */
	public void render(float pticks, int mouseX, int mouseY);
	
	/**
	 * Called when this component first enters the screen. Good time to refresh anything that
	 * can be dynamic.
	 */
	public default void onDisplayed(GuiScreen parent) {
		// NO-OP
	}

	/**
	 * Called on mouse click. Note that the click may not be inside your component, so
	 * you need to validate the position.
	 */
	public default void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		// NO-OP
	}
	
}
