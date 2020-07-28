package vazkii.patchouli.api.stub;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StubPatchouliAPI implements IPatchouliAPI {

	public static final StubPatchouliAPI INSTANCE = new StubPatchouliAPI();

	private StubPatchouliAPI() {}

	@Override
	public boolean isStub() {
		return true;
	}

	@Override
	public void setConfigFlag(String flag, boolean value) {
		// NO-OP
	}

	@Override
	public boolean getConfigFlag(String flag) {
		return false;
	}

	@Override
	public void openBookGUI(ServerPlayerEntity player, ResourceLocation book) {
		// NO-OP
	}

	@Override
	public void openBookEntry(ServerPlayerEntity player, ResourceLocation book, ResourceLocation entry, int page) {

	}

	@Override
	public void openBookGUI(ResourceLocation book) {
		// NO-OP
	}

	@Override
	public void openBookEntry(ResourceLocation book, ResourceLocation entry, int page) {}

	@Override
	public ResourceLocation getOpenBookGui() {
		return null;
	}

	@Override
	public ITextComponent getSubtitle(ResourceLocation bookId) {
		throw new IllegalArgumentException("Patchouli is not loaded");
	}

	@Override
	public void reloadBookContents() {
		// NO-OP
	}

	@Override
	public ItemStack getBookStack(ResourceLocation book) {
		return ItemStack.EMPTY;
	}

	@Override
	public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider) {
		// NO-OP
	}

	@Override
	public IMultiblock getMultiblock(ResourceLocation res) {
		return null;
	}

	@Override
	public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb) {
		return mb;
	}

	@Nullable
	@Override
	public IMultiblock getCurrentMultiblock() {
		return null;
	}

	@Override
	public void showMultiblock(IMultiblock multiblock, ITextComponent displayName, BlockPos center, Rotation rotation) {

	}

	@Override
	public void clearMultiblock() {

	}

	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return StubMultiblock.INSTANCE;
	}

	@Override
	public IMultiblock makeSparseMultiblock(Map<BlockPos, IStateMatcher> positions) {
		return StubMultiblock.INSTANCE;
	}

	@Override
	public IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher stateMatcher(BlockState state) {
		return StubMatcher.INSTANCE;
	}

	@Override
	public IStateMatcher propertyMatcher(BlockState state, Property<?>... properties) {
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
	public IStateMatcher displayOnlyMatcher(BlockState state) {
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
