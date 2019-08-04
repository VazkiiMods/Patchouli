package vazkii.patchouli.api;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	public Predicate<BlockState> getStatePredicate();

}
