package vazkii.patchouli.api.stub;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.IStyleStack;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.api.PatchouliConfigAccess;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
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

	@NotNull
	@Override
	public PatchouliConfigAccess getConfig() {
		return new PatchouliConfigAccess() {
			@Override
			public boolean disableAdvancementLocking() {
				return false;
			}

			@NotNull
			@Override
			public List<String> noAdvancementBooks() {
				return Collections.emptyList();
			}

			@Override
			public boolean testingMode() {
				return false;
			}

			@NotNull
			@Override
			public String inventoryButtonBook() {
				return "";
			}

			@Override
			public boolean useShiftForQuickLookup() {
				return false;
			}

			@NotNull
			@Override
			public TextOverflowMode overflowMode() {
				return TextOverflowMode.OVERFLOW;
			}

			@Override
			public int quickLookupTime() {
				return Integer.MAX_VALUE;
			}
		};
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
	public void openBookGUI(ServerPlayer player, ResourceLocation book) {
		// NO-OP
	}

	@Override
	public void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page) {

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
	public Component getSubtitle(ResourceLocation bookId) {
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
	public void showMultiblock(IMultiblock multiblock, Component displayName, BlockPos center, Rotation rotation) {

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

	@Override
	public IStateMatcher tagMatcher(TagKey<Block> block) {
		return StubMatcher.INSTANCE;
	}
}
