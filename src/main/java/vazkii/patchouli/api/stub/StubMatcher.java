package vazkii.patchouli.api.stub;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

public final class StubMatcher implements IStateMatcher {

	public static final StubMatcher INSTANCE = new StubMatcher();

	private final BlockState state = Blocks.AIR.getDefaultState();

	private StubMatcher() { }

	@Override
	public BlockState getDisplayedState(int ticks) {
		return state;
	}

	@Override
	public TriPredicate<BlockView, BlockPos, BlockState> getStatePredicate() {
		return (w, p, s) -> false;
	}

}
