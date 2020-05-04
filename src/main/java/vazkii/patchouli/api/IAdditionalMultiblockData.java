package vazkii.patchouli.api;

import javax.annotation.Nullable;

/**
 * Interface for getting additional multiblock data for checking
 */
@FunctionalInterface
public interface IAdditionalMultiblockData {
	@Nullable
	<T> T get(String name);
}
