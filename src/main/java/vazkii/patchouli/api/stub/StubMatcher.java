package vazkii.patchouli.api.stub;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.TriPredicate;
import vazkii.patchouli.api.IStateMatcher;

public class StubMatcher implements IStateMatcher {

	public static final StubMatcher INSTANCE = new StubMatcher();

	private BlockState state = Blocks.AIR.getDefaultState();

	private StubMatcher() { }

	@Override
	public BlockState getDisplayedState(int ticks) {
		return state;
	}

	@Override
	public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate() {
		return (w, p, s) -> false;
	}

}
