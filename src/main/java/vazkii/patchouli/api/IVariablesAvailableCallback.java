package vazkii.patchouli.api;

import java.util.function.UnaryOperator;

public interface IVariablesAvailableCallback {
	/**
	 * Called when variables are available, before the template component is built
	 * 
	 * @param lookup Call with arbitrary text, and it will be expanded:
	 *               first expanding all inline variables,
	 *               then attempting to apply "->" derivations if possible, otherwise looking up the string as a
	 *               plain variable from the template environment<br />
	 *               Gracefully handles nulls given as input, but will never return null itself.
	 */
	void onVariablesAvailable(UnaryOperator<IVariable> lookup);

}
