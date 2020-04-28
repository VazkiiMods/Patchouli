package vazkii.patchouli.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.TriPredicate;

/**
 * A composite element of a rendering block state, and a predicate
 * to validate if the real state in the world is valid or not. Used
 * as the core building block for multiblocks.
 */
public interface IStateMatcher {

	/**
	 * Gets the state displayed by this state matcher for rendering
	 * the multiblock page type and the in-world preview.
	 * 
	 * @param ticks World ticks, to allow cycling the state shown.
	 */
	BlockState getDisplayedState(int ticks);

	/**
	 * Returns a predicate that validates whether the given state is
	 * acceptable. This should check the passed in blockstate instead of requerying it from the world,
	 * for both performance and correctness reasons -- the state may be rotated for multiblock matching.
	 */
	TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate();

}
