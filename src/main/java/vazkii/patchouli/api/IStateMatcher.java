package vazkii.patchouli.api;

import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	@SideOnly(Side.CLIENT)
	public IBlockState getDisplayedState();
	
	/**
	 * Returns a predicate that validates whether the given state is 
	 * acceptable.
	 */
	public Predicate<IBlockState> getStatePredicate();

}
