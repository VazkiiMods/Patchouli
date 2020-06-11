package vazkii.patchouli.common.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import org.apache.commons.io.IOUtils;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.multiblock.DenseMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SparseMultiblock;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatchouliAPIImpl implements IPatchouliAPI {

	public static final PatchouliAPIImpl INSTANCE = new PatchouliAPIImpl();

	private PatchouliAPIImpl() {}

	@Override
	public boolean isStub() {
		return false;
	}

	@Override
	public void setConfigFlag(String flag, boolean value) {
		PatchouliConfig.setFlag(flag, value);
	}

	@Override
	public boolean getConfigFlag(String flag) {
		return PatchouliConfig.getConfigFlag(flag);
	}

	@Override
	public void openBookGUI(ServerPlayerEntity player, Identifier book) {
		MessageOpenBookGui.send(player, book, null, 0);
	}

	@Override
	public void openBookEntry(ServerPlayerEntity player, Identifier book, Identifier entry, int page) {
		MessageOpenBookGui.send(player, book, entry, page);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void openBookGUI(Identifier book) {
		ClientBookRegistry.INSTANCE.displayBookGui(book, null, 0);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void openBookEntry(Identifier book, Identifier entry, int page) {
		ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Identifier getOpenBookGui() {
		Screen gui = MinecraftClient.getInstance().currentScreen;
		if (gui instanceof GuiBook) {
			return ((GuiBook) gui).book.id;
		}
		return null;
	}

	@Nonnull
	@Override
	public Text getSubtitle(@Nonnull Identifier bookId) {
		Book book = BookRegistry.INSTANCE.books.get(bookId);
		if (book == null) {
			throw new IllegalArgumentException("Book not found: " + bookId);
		}
		return book.getSubtitle();
	}

	@Override
	public void reloadBookContents() {
		Patchouli.reloadBookHandler.run();
	}

	@Override
	public ItemStack getBookStack(Identifier book) {
		return ItemModBook.forBook(book);
	}

	@Override
	public void registerTemplateAsBuiltin(Identifier res, Supplier<InputStream> streamProvider) {
		InputStream testStream = streamProvider.get();
		if (testStream == null) {
			throw new NullPointerException("Stream provider can't return a null stream");
		}
		IOUtils.closeQuietly(testStream);

		Supplier<BookTemplate> prev = BookContents.addonTemplates.put(res, () -> {
			InputStream stream = streamProvider.get();
			InputStreamReader reader = new InputStreamReader(stream);
			return ClientBookRegistry.INSTANCE.gson.fromJson(reader, BookTemplate.class);
		});

		if (prev != null) {
			throw new IllegalArgumentException("Template " + res + " is already registered");
		}
	}

	@Override
	public ItemStack deserializeItemStack(String str) {
		return ItemStackUtil.loadStackFromString(str);
	}

	@Override
	public String serializeItemStack(ItemStack stack) {
		return ItemStackUtil.serializeStack(stack);
	}

	@Override
	public List<ItemStack> deserializeItemStackList(String str) {
		return ItemStackUtil.loadStackListFromString(str);
	}

	@Override
	public String serializeItemStackList(List<ItemStack> stacks) {
		return ItemStackUtil.serializeStackList(stacks);
	}

	@Override
	public Ingredient deserializeIngredient(String str) {
		return ItemStackUtil.loadIngredientFromString(str);
	}

	@Override
	public String serializeIngredient(Ingredient ingredient) {
		return ItemStackUtil.serializeIngredient(ingredient);
	}

	@Override
	public IMultiblock getMultiblock(Identifier res) {
		return MultiblockRegistry.MULTIBLOCKS.get(res);
	}

	@Override
	public IMultiblock registerMultiblock(Identifier res, IMultiblock mb) {
		return MultiblockRegistry.registerMultiblock(res, mb);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public IMultiblock getCurrentMultiblock() {
		return MultiblockVisualizationHandler.hasMultiblock ? MultiblockVisualizationHandler.getMultiblock() : null;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void showMultiblock(@Nonnull IMultiblock multiblock, @Nonnull Text displayName, @Nonnull BlockPos center, @Nonnull BlockRotation rotation) {
		MultiblockVisualizationHandler.setMultiblock(multiblock, displayName, null, false);
		MultiblockVisualizationHandler.anchorTo(center, rotation);
	}

	@Override
	public void clearMultiblock() {
		MultiblockVisualizationHandler.setMultiblock(null, (Text) null, null, false);
	}

	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return new DenseMultiblock(pattern, targets);
	}

	@Override
	public IMultiblock makeSparseMultiblock(Map<BlockPos, IStateMatcher> positions) {
		return new SparseMultiblock(positions);
	}

	@Override
	public IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher stateMatcher(BlockState state) {
		return StateMatcher.fromState(state);
	}

	@Override
	public IStateMatcher propertyMatcher(BlockState state, Property<?>... properties) {
		return StateMatcher.fromStateWithFilter(state, Arrays.asList(properties)::contains);
	}

	@Override
	public IStateMatcher looseBlockMatcher(Block block) {
		return StateMatcher.fromBlockLoose(block);
	}

	@Override
	public IStateMatcher strictBlockMatcher(Block block) {
		return StateMatcher.fromBlockStrict(block);
	}

	@Override
	public IStateMatcher displayOnlyMatcher(BlockState state) {
		return StateMatcher.displayOnly(state);
	}

	@Override
	public IStateMatcher displayOnlyMatcher(Block block) {
		return StateMatcher.displayOnly(block);
	}

	@Override
	public IStateMatcher airMatcher() {
		return StateMatcher.AIR;
	}

	@Override
	public IStateMatcher anyMatcher() {
		return StateMatcher.ANY;
	}

}
