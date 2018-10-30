package vazkii.patchouli.api;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.stub.StubPatchouliAPI;

public class PatchouliAPI {

	/**
	 * This is loaded by Patchouli to be a proper one at preInit.<br>
	 * Note that books are initialized by patchouli during postInit,
	 * so if you want to use the instance you either have to set your
	 * mod's dependencies to load after patchouli and use on preInit, 
	 * or use this only during init.
	 */
	public static IPatchouliAPI instance = StubPatchouliAPI.INSTANCE;

	public static interface IPatchouliAPI {
		
		// API
		public boolean isStub();
		
		// Book and Templates
		public void setConfigFlag(String flag, boolean value);
		public boolean getConfigFlag(String flag);
		public void reloadBookContents();
		public ItemStack getBookStack(String book);

		// ItemStack Serialization
		public ItemStack deserializeItemStack(String str);
		public String serializeItemStack(ItemStack stack);

		// Multiblocks
		public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb);
		public IMultiblock makeMultiblock(String[][] pattern, Object... targets);
		public IStateMatcher predicateMatcher(IBlockState display, Predicate<IBlockState> predicate);
		public IStateMatcher predicateMatcher(Block display, Predicate<IBlockState> predicate);
		public IStateMatcher stateMatcher(IBlockState state);
		public IStateMatcher looseBlockMatcher(Block block);
		public IStateMatcher strictBlockMatcher(Block block);
		public IStateMatcher displayOnlyMatcher(IBlockState state);
		public IStateMatcher displayOnlyMatcher(Block block);
		public IStateMatcher airMatcher();
		public IStateMatcher anyMatcher();
	}

}

