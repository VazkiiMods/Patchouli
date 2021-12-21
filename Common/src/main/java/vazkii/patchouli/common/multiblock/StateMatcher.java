package vazkii.patchouli.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

import java.util.Objects;
import java.util.function.Predicate;

public class StateMatcher implements IStateMatcher {

	public static final StateMatcher ANY = displayOnly(Blocks.AIR.defaultBlockState());
	public static final StateMatcher AIR = fromPredicate(Blocks.AIR.defaultBlockState(), (w, p, s) -> s.isAir());

	private final BlockState displayState;
	private final TriPredicate<BlockGetter, BlockPos, BlockState> statePredicate;

	private StateMatcher(BlockState displayState, TriPredicate<BlockGetter, BlockPos, BlockState> statePredicate) {
		this.displayState = displayState;
		this.statePredicate = statePredicate;
	}

	public static StateMatcher fromPredicate(BlockState display, Predicate<BlockState> predicate) {
		return new StateMatcher(display, (world, pos, state) -> predicate.test(state));
	}

	public static StateMatcher fromPredicate(Block display, Predicate<BlockState> predicate) {
		return fromPredicate(display.defaultBlockState(), predicate);
	}

	public static StateMatcher fromPredicate(BlockState display, TriPredicate<BlockGetter, BlockPos, BlockState> predicate) {
		return new StateMatcher(display, predicate);
	}

	public static StateMatcher fromPredicate(Block display, TriPredicate<BlockGetter, BlockPos, BlockState> predicate) {
		return new StateMatcher(display.defaultBlockState(), predicate);
	}

	public static StateMatcher fromState(BlockState displayState, boolean strict) {
		return fromPredicate(displayState,
				strict ? ((state) -> state == displayState)
						: ((state) -> state.getBlock() == displayState.getBlock()));
	}

	public static StateMatcher fromStateWithFilter(BlockState state, Predicate<Property<?>> filter) {
		return fromPredicate(state, state1 -> {
			if (state.getBlock() != state1.getBlock()) {
				return false;
			}
			return state1.getProperties()
					.stream()
					.filter(filter)
					.allMatch(property -> state1.hasProperty(property) &&
							state.hasProperty(property) &&
							Objects.equals(state.getValue(property), state1.getValue(property)));
		});
	}

	public static StateMatcher fromState(BlockState displayState) {
		return fromState(displayState, true);
	}

	public static StateMatcher fromBlockLoose(Block block) {
		return fromState(block.defaultBlockState(), false);
	}

	public static StateMatcher fromBlockStrict(Block block) {
		return fromState(block.defaultBlockState(), true);
	}

	public static StateMatcher displayOnly(BlockState state) {
		return new StateMatcher(state, (w, p, s) -> true);
	}

	public static StateMatcher displayOnly(Block block) {
		return displayOnly(block.defaultBlockState());
	}

	@Override
	public BlockState getDisplayedState(int ticks) {
		return displayState;
	}

	@Override
	public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
		return statePredicate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StateMatcher that = (StateMatcher) o;
		return statePredicate.equals(that.statePredicate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statePredicate);
	}
}
