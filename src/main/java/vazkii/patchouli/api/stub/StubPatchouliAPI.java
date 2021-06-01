package vazkii.patchouli.api.stub;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.IStyleStack;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
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
	public void openBookGUI(ServerPlayerEntity player, Identifier book) {
		// NO-OP
	}

	@Override
	public void openBookEntry(ServerPlayerEntity player, Identifier book, Identifier entry, int page) {

	}

	@Override
	public void openBookGUI(Identifier book) {
		// NO-OP
	}

	@Override
	public void openBookEntry(Identifier book, Identifier entry, int page) {}

	@Override
	public Identifier getOpenBookGui() {
		return null;
	}

	@Override
	public Text getSubtitle(Identifier bookId) {
		throw new IllegalArgumentException("Patchouli is not loaded");
	}

	@Override
	public void registerCommand(String name, Function<IStyleStack, String> command) {
		// NO-OP
	}

	@Override
	public void registerFunction(String name, BiFunction<String, IStyleStack, String> function) {
		// NO-OP
	}

	@Override
	public void reloadBookContents() {
		// NO-OP
	}

	@Override
	public ItemStack getBookStack(Identifier book) {
		return ItemStack.EMPTY;
	}

	@Override
	public void registerTemplateAsBuiltin(Identifier res, Supplier<InputStream> streamProvider) {
		// NO-OP
	}

	@Override
	public IMultiblock getMultiblock(Identifier res) {
		return null;
	}

	@Override
	public IMultiblock registerMultiblock(Identifier res, IMultiblock mb) {
		return mb;
	}

	@Nullable
	@Override
	public IMultiblock getCurrentMultiblock() {
		return null;
	}

	@Override
	public void showMultiblock(IMultiblock multiblock, Text displayName, BlockPos center, BlockRotation rotation) {

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
