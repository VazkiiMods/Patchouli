package vazkii.patchouli.api;

import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		
		// ================================================================================================
		// API
		// ================================================================================================
		
		/**
		 * Returns false if this is a real API class loaded by the actual mod.
		 */
		public boolean isStub();
		
		// ================================================================================================
		// Book and Templates
		// ================================================================================================
		
		/**
		 * Sets a config flag to the value passed.<br>
		 * IMPORTANT: DO NOT call this without your flag being prefixed with your
		 * mod id. There is no protection against that, but don't be a jerk.
		 */
		public void setConfigFlag(String flag, boolean value);
		
		/**
		 * Gets the value of a config flag, or false if it doesn't have a value.
		 */
		public boolean getConfigFlag(String flag);
		
		/**
		 * Opens a book GUI for the given player (server version).
		 */
		public void openBookGUI(ServerPlayerEntity player, ResourceLocation book);
		
		/**
		 * Opens a book GUI. (client version)
		 */
		@OnlyIn(Dist.CLIENT) public void openBookGUI(ResourceLocation book);
		
		@OnlyIn(Dist.CLIENT) public ResourceLocation getOpenBookGui();
		
		/**
		 * Reloads the contents of all books. Call sparingly and only if you
		 * really need it for whatever reason.
		 */
		public void reloadBookContents();
		
		/**
		 * Returns a book item with its NBT set to the book passed in.
		 */
		public ItemStack getBookStack(String book);
		
		/**
		 * Register a template you made as a built in template to be used with all books
		 * as the "res" resource location. The supplier should give an input stream that
		 * reads a full json file, containing a template.
		 */
		@OnlyIn(Dist.CLIENT) public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider);

		// ================================================================================================
		// ItemStack Serialization
		// ================================================================================================
		
		/**
		 * Deserializes a stack string into its ItemStack.
		 */
		public ItemStack deserializeItemStack(String str);
		
		/**
		 * Serializes an ItemStack into its string correspondent.
		 */
		public String serializeItemStack(ItemStack stack);
		
		/**
		 * Serializes an ingredient string into a list of ItemStacks.
		 */
		public List<ItemStack> deserializeItemStackList(String str);
		
		/**
		 * Serializes a list of ItemStacks into its string correspondent.
		 */
		public String serializeItemStackList(List<ItemStack> stacks);

		// ================================================================================================
		// Ingredient Serialization
		// ================================================================================================
		
		/**
		 * Deserializes an ingredient string into its Ingredient.
		 */
		public Ingredient deserializeIngredient(String str);
		
		/**
		 * Serializes an Ingredient into its string correspondent.
		 */
		public String serializeIngredient(Ingredient ingredient);

		// ================================================================================================
		// Multiblocks
		// ================================================================================================
		
		/**
		 * Gets a multiblock by its resource location, or null if none exists for it.
		 */
		public IMultiblock getMultiblock(ResourceLocation res);
		
		/**
		 * Registers a multiblock given its resource location. This takes care of both registering it
		 * and setting its resource location to the one passed.
		 */
		public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb);
		
		/**
		 * Creates a multiblock given the pattern and targets given. This works in the same way as 
		 * recipe registrations do, except it's a 2D array. The pattern works in the same way as
		 * you'd register a multiblock using JSON. Check the page on Multiblocks on the Patchouli
		 * wiki for more info.
		 * <br><br>
		 * As for the target array, it's in also in the same format as recipes. One char followed
		 * by one Object, so on and so forth, defining each type. The Object can be a Block, an
		 * BlockState, or an IStateMatcher.
		 */
		public IMultiblock makeMultiblock(String[][] pattern, Object... targets);
		
		/**
		 * Gets an IStateMatcher with the passed in BlockState for display and the passed in
		 * predicate for validation.
		 */
		public IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate);
		
		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and the
		 * passed in predicate for validation.
		 */
		public IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate);
		
		/**
		 * Gets an IStateMatcher with the passed in BlockState for display and validation, 
		 * requiring that the state in world be exactly the same.
		 */
		public IStateMatcher stateMatcher(BlockState state);
		
		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and 
		 * validation, requiring that the state in world have only the same block.
		 */
		public IStateMatcher looseBlockMatcher(Block block);
		
		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and 
		 * validation, requiring that the state in world be exactly the same.
		 */
		public IStateMatcher strictBlockMatcher(Block block);
		
		/**
		 * Gets an IStateMatcher that always validates to true, and shows the BlockState
		 * passed when displayed.
		 */
		public IStateMatcher displayOnlyMatcher(BlockState state);
		
		/**
		 * Gets an IStateMatcher that always validates to true, and shows the passed in
		 * Block's default state when displayed.
		 */
		public IStateMatcher displayOnlyMatcher(Block block);
		
		/**
		 * Gets an IStateMatcher that accepts only air blocks.
		 */
		public IStateMatcher airMatcher();
		
		/**
		 * Gets an IStateMatcher that accepts anything.
		 */
		public IStateMatcher anyMatcher();
	}

}

