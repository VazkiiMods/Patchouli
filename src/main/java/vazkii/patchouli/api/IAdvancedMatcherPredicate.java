package vazkii.patchouli.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * A predicate that checks the given state and returns a boolean.
 */
@FunctionalInterface
public interface IAdvancedMatcherPredicate {
	boolean test(IBlockReader world, BlockPos pos, BlockState state, IAdditionalMultiblockData additionalData);
}
