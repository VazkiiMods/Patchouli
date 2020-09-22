package vazkii.patchouli.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import vazkii.patchouli.api.stub.StubPatchouliAPI;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatchouliAPI {

	/**
	 * This is loaded by Patchouli to be a proper one at mod construction time.<br>
	 * Note that books are initialized by patchouli on in common setup, but contents are not loaded until
	 * the client logs in to a world.
	 */
	public static IPatchouliAPI instance = StubPatchouliAPI.INSTANCE;

	public interface IPatchouliAPI {

		// ================================================================================================
		// API
		// ================================================================================================

		/**
		 * Returns false if this is a real API class loaded by the actual mod.
		 */
		boolean isStub();

		// ================================================================================================
		// Book and Templates
		// ================================================================================================

		/**
		 * Sets a config flag to the value passed.<br>
		 * This is safe to call during parallel mod loading.<br>
		 * IMPORTANT: DO NOT call this without your flag being prefixed with your
		 * mod id. There is no protection against that, but don't be a jerk.
		 */
		void setConfigFlag(String flag, boolean value);

		/**
		 * Gets the value of a config flag, or false if it doesn't have a value.
		 */
		boolean getConfigFlag(String flag);

		/**
		 * Opens the given book to the last page that was open, or the landing page otherwise.
		 */
		void openBookGUI(ServerPlayerEntity player, ResourceLocation book);

		/**
		 * Opens the book to the given entry
		 */
		void openBookEntry(ServerPlayerEntity player, ResourceLocation book, ResourceLocation entry, int page);

		/**
		 * Client version of {@link #openBookGUI(ServerPlayerEntity, ResourceLocation)}.
		 */
		void openBookGUI(ResourceLocation book);

		/**
		 * Client version of {@link #openBookEntry(ServerPlayerEntity, ResourceLocation, ResourceLocation, int)}
		 */
		void openBookEntry(ResourceLocation book, ResourceLocation entry, int page);

		/**
		 * @return Book ID of the currently open book, null if none open
		 */
		@Nullable
		ResourceLocation getOpenBookGui();

		/**
		 * @return                          The subtitle (edition string/what appears under the title in the landing
		 *                                  page) of the book.
		 * @throws IllegalArgumentException if the book id given cannot be found
		 */
		ITextComponent getSubtitle(ResourceLocation bookId);

		/**
		 * Reloads the contents of all books. Call sparingly and only if you
		 * really need it for whatever reason.
		 */
		void reloadBookContents();

		/**
		 * Returns a book item with its NBT set to the book passed in.
		 */
		ItemStack getBookStack(ResourceLocation book);

		/**
		 * Register a template you made as a built in template to be used with all books
		 * as the "res" resource location. The supplier should give an input stream that
		 * reads a full json file, containing a template.
		 */
		@OnlyIn(Dist.CLIENT)
		void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider);

		// ================================================================================================
		// Multiblocks
		// ================================================================================================

		/**
		 * Gets a multiblock by its resource location, or null if none exists for it.
		 */
		IMultiblock getMultiblock(ResourceLocation res);

		/**
		 * Registers a multiblock given its resource location. This takes care of both registering it
		 * and setting its resource location to the one passed.
		 */
		IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb);

		/**
		 * @return The multiblock currently being visualized in-world or null if no multiblock is visualized. Only works
		 *         clientside.
		 */
		@Nullable
		IMultiblock getCurrentMultiblock();

		/**
		 * Sets the given multiblock as the currently visualized one. This overwrites any currently visualized
		 * multiblock.
		 * Only works clientside.
		 * 
		 * @param multiblock  The multiblock to visualize
		 * @param displayName The name to show above the completion bar
		 * @param center      Where to place the multiblock's center
		 * @param rotation    Orientation to visualize
		 */
		void showMultiblock(IMultiblock multiblock, ITextComponent displayName, BlockPos center, Rotation rotation);

		/**
		 * Clears the currently visualized multiblock. Only works clientside.
		 */
		void clearMultiblock();

		/**
		 * Creates a multiblock given the pattern and targets given. This works in the same way as
		 * recipe registrations do, except it's a 2D array. The pattern works in the same way as
		 * you'd register a multiblock using JSON. Check the page on Multiblocks on the Patchouli
		 * wiki for more info.
		 * <br>
		 * <br>
		 * As for the target array, it's in also in the same format as recipes. One char followed
		 * by one Object, so on and so forth, defining each type. The Object can be a Block, an
		 * BlockState, or an IStateMatcher.
		 */
		IMultiblock makeMultiblock(String[][] pattern, Object... targets);

		/**
		 * Create a sparse multiblock. This is useful in situations where the multiblock is large and unwieldy
		 * to specify in a 2D grid. The center of a sparse multiblock is always (0, 0, 0), and the keys
		 * of {@code positions} are relative to that space.
		 */
		IMultiblock makeSparseMultiblock(Map<BlockPos, IStateMatcher> positions);

		/**
		 * Gets an IStateMatcher with the passed in BlockState for display and the passed in
		 * predicate for validation.
		 */
		IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate);

		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and the
		 * passed in predicate for validation.
		 */
		IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate);

		/**
		 * Gets an IStateMatcher with the passed in BlockState for display and validation,
		 * requiring that the state in world be exactly the same.
		 */
		IStateMatcher stateMatcher(BlockState state);

		/**
		 * Gets an IStateMatcher with the passed in BlockState for display and validation,
		 * requiring that only the specified properties are the same.
		 */
		IStateMatcher propertyMatcher(BlockState state, Property<?>... properties);

		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and
		 * validation, requiring that the state in world have only the same block.
		 */
		IStateMatcher looseBlockMatcher(Block block);

		/**
		 * Gets an IStateMatcher with the passed in Block's default state for display and
		 * validation, requiring that the state in world be exactly the same.
		 */
		IStateMatcher strictBlockMatcher(Block block);

		/**
		 * Gets an IStateMatcher that always validates to true, and shows the BlockState
		 * passed when displayed.
		 */
		IStateMatcher displayOnlyMatcher(BlockState state);

		/**
		 * Gets an IStateMatcher that always validates to true, and shows the passed in
		 * Block's default state when displayed.
		 */
		IStateMatcher displayOnlyMatcher(Block block);

		/**
		 * Gets an IStateMatcher that accepts only air blocks.
		 */
		IStateMatcher airMatcher();

		/**
		 * Gets an IStateMatcher that accepts anything.
		 */
		IStateMatcher anyMatcher();
	}

}
