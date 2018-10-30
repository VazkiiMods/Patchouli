package vazkii.patchouli.api;

import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;

public interface IStateMatcher {

	public IBlockState getDisplayedState();
	public Predicate<IBlockState> getStatePredicate();

}
