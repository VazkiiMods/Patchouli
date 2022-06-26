package vazkii.patchouli.api;

import com.google.common.base.Suppliers;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vazkii.patchouli.api.stub.StubPatchouliAPI;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatchouliAPI {
	private static final Supplier<IPatchouliAPI> LAZY_INSTANCE = Suppliers.memoize(() -> {
		try {
			return (IPatchouliAPI) Class.forName("vazkii.patchouli.common.base.PatchouliAPIImpl").newInstance();
		} catch (ReflectiveOperationException e) {
			LogManager.getLogger().warn("Unable to find PatchouliAPIImpl, using a dummy");
			return StubPatchouliAPI.INSTANCE;
		}
	});

	/**
	 * Obtain the Patchouli API, either a valid implementation if Patchouli is present, else
	 * a no-op stub instance if Patchouli is absent.
	 * Note that many API functions make no sense to call before books are loaded (which is on login),
	 * please use common sense and your best judgment.
	 */
	public static IPatchouliAPI get() {
		return LAZY_INSTANCE.get();
	}

	public static final String MOD_ID = "patchouli";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

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
		 * Sends a network message to the given player
		 * to open the given book to the last page that was open, or the landing page otherwise.
		 */
		void openBookGUI(ServerPlayer player, ResourceLocation book);

		/**
		 * Sends a network message to the given player
		 * to open the book to the given entry
		 * 
		 * @param page Zero-indexed page number
		 */
		void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page);

		/**
		 * Client version of {@link #openBookGUI(ServerPlayer, ResourceLocation)}.
		 */
		void openBookGUI(ResourceLocation book);

		/**
		 * Client version of {@link #openBookEntry(ServerPlayer, ResourceLocation, ResourceLocation, int)}
		 */
		void openBookEntry(ResourceLocation book, ResourceLocation entry, int page);

		/**
		 * Returns the book ID of the currently open book, if any. Only works clientside.
		 */
		@Nullable
		ResourceLocation getOpenBookGui();

		/**
		 * Works on both sides.
		 * 
		 * @return                          The subtitle (edition string/what appears under the title in the landing
		 *                                  page) of the book.
		 * @throws IllegalArgumentException if the book id given cannot be found
		 */
		Component getSubtitle(ResourceLocation bookId);

		/**
		 * Returns a book item with its NBT set to the book passed in. Works on both sides.
		 */
		ItemStack getBookStack(ResourceLocation book);

		/**
		 * Register a template you made as a built in template to be used with all books
		 * as the "res" resource location. The supplier should give an input stream that
		 * reads a full json file, containing a template.
		 * Only works on client.
		 */
		void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider);

		/**
		 * Register a Patchouli command, of the type $(cmdname).
		 * A command gets an IStyleStack if it wishes to modify it (for example, $(o) italicizes),
		 * and returns the text that should replace the command (for example, $(playername) is replaced
		 * with the current player's username). Commands that only modify style should return "".
		 * This is thread safe. Only works on client.
		 */
		void registerCommand(String name, Function<IStyleStack, String> command);

		/**
		 * Register a Patchouli function, of the type $(funcname:arg).
		 * A function is like a command,
		 * except it gets an additional argument (the text after the colon),
		 * for things like conditional formatting or differing return values.
		 * For example, $(k:use) is replaced by Right Button by default.
		 * This is thread safe. Only works on client.
		 */
		void registerFunction(String name, BiFunction<String, IStyleStack, String> function);

		// ================================================================================================
		// Multiblocks
		// ================================================================================================

		/**
		 * Gets a multiblock by its ID, or null if none exists for it.
		 */
		@Nullable
		IMultiblock getMultiblock(ResourceLocation id);

		/**
		 * Registers a multiblock given its resource location. This takes care of both registering it
		 * and setting its resource location to the one passed.
		 */
		IMultiblock registerMultiblock(ResourceLocation id, IMultiblock mb);

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
		void showMultiblock(IMultiblock multiblock, Component displayName, BlockPos center, Rotation rotation);

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
		 * Creates an IStateMatcher using the passed in BlockState for display and the passed in
		 * predicate for validation.
		 */
		IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate);

		/**
		 * Creates an IStateMatcher with the passed in Block's default state for display and the
		 * passed in predicate for validation.
		 */
		IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate);

		/**
		 * Creates an IStateMatcher with the passed in BlockState for display and validation,
		 * requiring that the state in world be exactly the same.
		 */
		IStateMatcher stateMatcher(BlockState state);

		/**
		 * Creates an IStateMatcher with the passed in BlockState for display and validation,
		 * requiring that only the specified properties are the same.
		 */
		IStateMatcher propertyMatcher(BlockState state, Property<?>... properties);

		/**
		 * Creates an IStateMatcher with the passed in Block's default state for display and
		 * validation, requiring that the state in world have only the same block.
		 */
		IStateMatcher looseBlockMatcher(Block block);

		/**
		 * Creates an IStateMatcher with the passed in Block's default state for display and
		 * validation, requiring that the state in world be exactly the same.
		 */
		IStateMatcher strictBlockMatcher(Block block);

		/**
		 * Creates an IStateMatcher that always validates to true, and shows the BlockState
		 * passed when displayed.
		 */
		IStateMatcher displayOnlyMatcher(BlockState state);

		/**
		 * Creates an IStateMatcher that always validates to true, and shows the passed in
		 * Block's default state when displayed.
		 */
		IStateMatcher displayOnlyMatcher(Block block);

		/**
		 * @return A state matcher that accepts any block in the given tag
		 */
		IStateMatcher tagMatcher(TagKey<Block> block);

		/**
		 * Creates an IStateMatcher that accepts only air blocks.
		 */
		IStateMatcher airMatcher();

		/**
		 * Creates an IStateMatcher that accepts anything.
		 */
		IStateMatcher anyMatcher();
	}

}
