package vazkii.patchouli.api;

import net.minecraft.block.BlockState;

/**
 * Advanced version of {@link IStateMatcher} with support for {@link IAdditionalMultiblockData}
 */
public interface IAdvancedStateMatcher extends IStateMatcher {
	/**
	 * Returns a predicate that validates whether the given state is
	 * acceptable. This should check the passed in blockstate instead of requerying it from the world,
	 * for both performance and correctness reasons -- the state may be rotated for multiblock matching.
	 */
	IAdvancedMatcherPredicate getAdvancedStatePredicate();

	/**
	 * Gets the state displayed by this state matcher for rendering
	 * the multiblock page type and the in-world preview.
	 *
	 * @param ticks World ticks, to allow cycling the state shown.
	 * @param data  Additional data, to allow for advanced behaviour
	 */
	BlockState getDisplayedState(int ticks, IAdditionalMultiblockData data);
}
