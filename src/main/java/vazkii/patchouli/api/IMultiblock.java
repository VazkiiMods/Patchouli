package vazkii.patchouli.api;

import java.util.function.Consumer;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An instance of a multiblock.
 * <br><br>
 * WARNING: This interface is provided only for usage with the API. For creating
 * an IMultiblock instance use the methods provided in the API main class. Please
 * do not create your own implementation of this, as it'll not be compatible with
 * all the features in the mod.
 */
public interface IMultiblock {

	// ================================================================================================
	// Builder methods
	// ================================================================================================
	
	/**
	 * Offsets the position of the multiblock by the amount specified.
	 * Works for both placement, validation, and rendering.
	 */
	public IMultiblock offset(int x, int y, int z);
	
	/**
	 * Offsets the view of the multiblock by the amount specified.
	 * Matters only for where the multiblock renders.
	 */
	public IMultiblock offsetView(int x, int y, int z);
	
	/**
	 * Sets the multiblock's symmetrical value. Symmetrical multiblocks
	 * check only in one rotation, not all 4. If your multiblock is symmetrical
	 * around the center axis, set this to true to prevent needless cycles.
	 */
	public IMultiblock setSymmetrical(boolean symmetrical);
	
	/**
	 * Sets the multiblock's resource location. Not something you need to
	 * call yourself as the register method in the main API class does it for you.
	 */
	public IMultiblock setResourceLocation(ResourceLocation res);
	
	// ================================================================================================
	// Getters
	// ================================================================================================
	
	/**
	 * Gets if this multiblock is symmetrical.
	 * @see IMultiblock#setSymmetrical
	 */
	public boolean isSymmetrical();

	// ================================================================================================
	// Actual functionality
	// Note: DO NOT USE THESE METHODS IF YOUR MOD DOESN'T HAVE
	// A HARD DEPENDENCY ON PATCHOULI
	//
	// The stub API will return an empty multiblock that doesn't
	// do any of these things!
	// ================================================================================================
	
	/**
	 * Places the multiblock at the given position with the given rotation.
	 */
	public void place(World world, BlockPos pos, Rotation rotation);
	
	/**
	 * Runs "action" at every instance of the character "c" in the multiblock 
	 * at the given position with the given rotation. You can use this to
	 * run actions on a few special blocks. "c" is a character corresponding
	 * to one of the characters used when creating the multiblock.
	 */
	public void forEach(World world, BlockPos pos, Rotation rotation, char c, Consumer<BlockPos> action);
	
	/**
	 * Partially validates a multiblock. Returns true when 'acceptor' returns
	 * true for every instance of the character c in the multiblock at the
	 * given position with the given rotation. "c" is a character corresponding
	 * to one of the characters used when creating the multiblock.

	 */
	public boolean forEachMatcher(World world, BlockPos pos, Rotation rotation, char c, MatcherAcceptor acceptor);
	
	/**
	 * Validates if the multiblock exists at the given position. Will check all 4
	 * rotations if the multiblock is not symmetrical. 
	 */
	
	public boolean validate(World world, BlockPos pos);
	
	/**
	 * Tests if any one given block of the multiblock exists at the given position
	 * with the given rotation. x, y, and z must be constrained within the multiblock's
	 * sizes.
	 */
	public boolean test(World world, BlockPos start, int x, int y, int z, Rotation rotation);

	// Functional Interfaces

	public interface MatcherAcceptor {
		boolean accepts(BlockPos start, BlockPos actionPos, int x, int y, int z, char c, IStateMatcher matcher);
	}

}
