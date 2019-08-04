package vazkii.patchouli.common.multiblock;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import vazkii.patchouli.api.IStateMatcher;

public class StateMatcher implements IStateMatcher {

	public static final StateMatcher ANY = displayOnly(Blocks.AIR.getDefaultState());
	public static final StateMatcher AIR = fromState(Blocks.AIR.getDefaultState());

	private final BlockState displayState;
	private final Predicate<BlockState> statePredicate;

	private StateMatcher(BlockState displayState, Predicate<BlockState> statePredicate) {
		this.displayState = displayState;
		this.statePredicate = statePredicate;
	}

	public static StateMatcher fromPredicate(BlockState display, Predicate<BlockState> predicate) {
		return new StateMatcher(display, predicate);
	}

	public static StateMatcher fromPredicate(Block display, Predicate<BlockState> predicate) {
		return fromPredicate(display.getDefaultState(), predicate);
	}

	public static StateMatcher fromState(BlockState displayState, boolean strict) {
		return new StateMatcher(displayState,
				strict ? ((state) -> state.getBlock() == displayState.getBlock() && state.getProperties().equals(displayState.getProperties()))
						: ((state) -> state.getBlock() == displayState.getBlock()));
	}

	public static StateMatcher fromState(BlockState displayState) {
		return fromState(displayState, true);
	}

	public static StateMatcher fromBlockLoose(Block block) {
		return fromState(block.getDefaultState(), false);
	}

	public static StateMatcher fromBlockStrict(Block block) {
		return fromState(block.getDefaultState(), true);
	}

	public static StateMatcher displayOnly(BlockState state) {
		return new StateMatcher(state, (s) -> true);
	}

	public static StateMatcher displayOnly(Block block) {
		return displayOnly(block.getDefaultState());
	}

	@Override
	public BlockState getDisplayedState() {
		return displayState;
	}

	@Override
	public Predicate<BlockState> getStatePredicate() {
		return statePredicate;
	}

}