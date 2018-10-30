package vazkii.patchouli.api;

import java.util.function.Consumer;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * WARNING: This interface is provided only for usage with the API. For creating
 * an IMultiblock instance use the methods provided in the API main class. Please
 * do not create your own implementation of this, as it'll not be compatible with
 * all the features in the mod.
 */
public interface IMultiblock {

	// Builder methods
	public IMultiblock offset(int x, int y, int z);
	public IMultiblock offsetView(int x, int y, int z); 
	public IMultiblock setSymmetrical(boolean symmetrical);
	public IMultiblock setResourceLocation(ResourceLocation res);

	// Getters
	public boolean isSymmetrical();

	// Actual functionality
	// Note: DO NOT USE THESE METHODS IF YOUR MOD DOESN'T HAVE
	// A HARD DEPENDENCY ON PATCHOULI
	//
	// The stub API will return an empty multiblock that doesn't
	// do any of these things!

	public void place(World world, BlockPos pos, Rotation rotation);
	public void forEach(World world, BlockPos pos, Rotation rotation, char c, Consumer<BlockPos> action);
	public boolean forEachMatcher(World world, BlockPos pos, Rotation rotation, char c, MatcherAcceptor acceptor);
	public boolean validate(World world, BlockPos pos);
	public boolean test(World world, BlockPos start, int x, int y, int z, Rotation rotation);

	// Functional Interfaces

	public interface MatcherAcceptor {
		boolean accepts(BlockPos start, BlockPos actionPos, int x, int y, int z, char c, IStateMatcher matcher);
	}

}
