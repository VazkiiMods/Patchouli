package vazkii.patchouli.api;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface IVariableAvailableCallback {
	/**
	 * Called when variables are available, before the template component is built
	 * @param lookup Variable lookup, call with the full name of the desired variable to receive
	 *               the variable value back. <b>May return null</b> if the queried variable couldn't be found.
	 *               Gracefully handles nulls given as input.
	 */
	void onVariablesAvailable(Function<String, String> lookup);

}
