package vazkii.patchouli.api.stub;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import vazkii.patchouli.api.IStateMatcher;

public class StubMatcher implements IStateMatcher {

	public static final StubMatcher INSTANCE = new StubMatcher();

	private BlockState state = Blocks.AIR.getDefaultState();

	private StubMatcher() { }

	@Override
	public BlockState getDisplayedState() {
		return state;
	}

	@Override
	public Predicate<BlockState> getStatePredicate() {
		return s -> false;
	}

}
