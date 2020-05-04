package vazkii.patchouli.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * Advanced version of {@link IStateMatcher} with support for {@link IAdditionalMultiblockData}
 */
public interface IAdvancedStateMatcher extends IStateMatcher {
	/**
	 * Returns a predicate that validates whether the given state is
	 * acceptable. This should check the passed in blockstate instead of requerying it from the world,
	 * for both performance and correctness reasons -- the state may be rotated for multiblock matching.
	 */
	QuadPredicate<IBlockReader, BlockPos, BlockState, IAdditionalMultiblockData> getAdvancedStatePredicate();
}
