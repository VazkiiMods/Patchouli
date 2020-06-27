package vazkii.patchouli.api;

import com.mojang.blaze3d.matrix.MatrixStack;

/**
 * An interface for API level custom components for templates.
 * <br>
 * <br>
 * <b>WARNING:</b> Any fields you declare in instances of this will be deserialized from
 * gson. Any fields that you don't want to be read from the json should have
 * the transient keyword.
 */
public interface ICustomComponent extends IVariablesAvailableCallback {

	/**
	 * Called when this component is built, after variables have been resolved with {@link #onVariablesAvailable}.
	 * Take the chance to parse String variables into game objects, perform error checking, and setup any local
	 * positions here.
	 */
	void build(int componentX, int componentY, int pageNum);

	/**
	 * Called every render tick. No special transformations are applied, so you're responsible
	 * for putting everything in the right place.
	 */
	void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY);

	/**
	 * Called when this component first enters the screen. Good time to refresh anything that
	 * can be dynamic. If you need to add buttons, you can add them here too.
	 */
	default void onDisplayed(IComponentRenderContext context) {
		// NO-OP
	}

	/**
	 * Called on mouse click. Note that the click may not be inside your component, so
	 * you need to validate the position.
	 */
	default boolean mouseClicked(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton) {
		return false;
	}

}
