package vazkii.patchouli.api;

/**
 * An interface for API level custom components for templates.
 * <br><br>
 * <b>WARNING:</b> Any fields you declare in instances of this will be deserialized from
 * gson. Any fields that you don't want to be read from the json should have
 * the transient keyword. 
 * <br><br>
 * Non-transient fields you declare in instances of this may have the
 * {@link VariableHolder} annotation associated to them, and as such, they'll load variables
 * the same way any built-in template would. Any fields with a @VariableHolder must be public.
 * <br><br>
 * The following types may be variable holders:
 * <ul>
 * <li> String </li>
 * <li> String[] </li>
 * <li> List (as String list)</li>
 * <li> Map (as String -> String map)</li>
 * <li> Object (any object can be VariableHolder, causing the variable assigner to deepen its
 * search into the VariableHolder fields that it contains) </li>
 * </ul>
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
	public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY);
	
	/**
	 * Called when this component first enters the screen. Good time to refresh anything that
	 * can be dynamic. If you need to add buttons, you can add them here too.
	 */
	public default void onDisplayed(IComponentRenderContext context) {
		// NO-OP
	}

	/**
	 * Called on mouse click. Note that the click may not be inside your component, so
	 * you need to validate the position.
	 */
	public default void mouseClicked(IComponentRenderContext context, int mouseX, int mouseY, int mouseButton) {
		// NO-OP
	}
	
}
