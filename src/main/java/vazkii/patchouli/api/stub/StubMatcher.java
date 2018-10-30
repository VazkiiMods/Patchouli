package vazkii.patchouli.api.stub;

import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import vazkii.patchouli.api.IStateMatcher;

public class StubMatcher implements IStateMatcher {

	public static final StubMatcher INSTANCE = new StubMatcher();

	private IBlockState state = Blocks.AIR.getDefaultState();

	private StubMatcher() { }

	@Override
	public IBlockState getDisplayedState() {
		return state;
	}

	@Override
	public Predicate<IBlockState> getStatePredicate() {
		return s -> false;
	}

}
