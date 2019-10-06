package vazkii.patchouli.api;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	 */
	@OnlyIn(Dist.CLIENT)
	public BlockState getDisplayedState();
	
	/**
	 * Returns a predicate that validates whether the given state is 
	 * acceptable.
	 */
	public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate();

}
