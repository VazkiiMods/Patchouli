package vazkii.patchouli.api.stub;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

public class StubPatchouliAPI implements IPatchouliAPI {

	public static final StubPatchouliAPI INSTANCE = new StubPatchouliAPI();

	private StubPatchouliAPI() { }

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
	public void openBookEntry(Identifier book, Identifier entry, int page) {
	}

	@Override
	public Identifier getOpenBookGui() {
		return null;
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
	public ItemStack deserializeItemStack(String str) {
		return ItemStack.EMPTY;
	}

	@Override
	public String serializeItemStack(ItemStack stack) {
		return "";
	}

	@Override
	public List<ItemStack> deserializeItemStackList(String str) {
		return Collections.emptyList();
	}

	@Override
	public String serializeItemStackList(List<ItemStack> stacks) {
		return "";
	}
	
	@Override
	public Ingredient deserializeIngredient(String str) {
		return Ingredient.EMPTY;
	}

	@Override
	public String serializeIngredient(Ingredient ingredient) {
		return "";
	}

	@Override
	public IMultiblock getMultiblock(Identifier res) {
		return null;
	}
	
	@Override
	public IMultiblock registerMultiblock(Identifier res, IMultiblock mb) {
		return mb;
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
