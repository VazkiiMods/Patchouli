package vazkii.patchouli.api.stub;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

public class StubPatchouliAPI implements IPatchouliAPI {

	public static final StubPatchouliAPI INSTANCE = new StubPatchouliAPI();

	private StubPatchouliAPI() { }

	@Override
	public void setConfigFlag(String flag, boolean value) {
		// NO-OP
	}

	@Override
	public boolean getConfigFlag(String flag) {
		return false;
	}

	@Override
	public void reloadBookContents() {
		// NO-OP
	}

	@Override
	public ItemStack deserializeItemStack(String str) {
		return ItemStack.EMPTY;
	}

	@Override
	public String serializeItemStack(ItemStack stack) {
		return "";
	}

	@Override
	public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb) {
		return mb;
	}

	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return StubMultiblock.INSTANCE;
	}

	@Override
	public IStateMatcher predicateMatcher(IBlockState display, Predicate<IBlockState> predicate) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<IBlockState> predicate) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher stateMatcher(IBlockState state) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher looseBlockMatcher(Block block) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher strictBlockMatcher(Block block) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher displayOnlyMatcher(IBlockState state) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher displayOnlyMatcher(Block block) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher airMatcher() {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher anyMatcher() {
		return StubMatcher.INSTANCE;
	}

}
