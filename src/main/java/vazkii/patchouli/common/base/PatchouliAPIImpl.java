package vazkii.patchouli.common.base;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.multiblock.Multiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.util.ItemStackUtil;

public class PatchouliAPIImpl implements IPatchouliAPI {

	public static final PatchouliAPIImpl INSTANCE = new PatchouliAPIImpl();
	
	private PatchouliAPIImpl() { }
	
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
	public void openBookGUI(EntityPlayerMP player, ResourceLocation book) {
		NetworkHandler.INSTANCE.sendTo(new MessageOpenBookGui(book.toString()), (EntityPlayerMP) player);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void openBookGUI(ResourceLocation book) {
		ClientBookRegistry.INSTANCE.displayBookGui(book.toString());
	}
	
	@Override
	public void reloadBookContents() {
		Patchouli.proxy.requestBookReload();
	}
	
	@Override
	public ItemStack getBookStack(String book) {
		return ItemModBook.forBook(book);
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
	public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb) {
		return MultiblockRegistry.registerMultiblock(res, mb);
	}

	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return new Multiblock(pattern, targets);
	}

	@Override
	public IStateMatcher predicateMatcher(IBlockState display, Predicate<IBlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<IBlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher stateMatcher(IBlockState state) {
		return StateMatcher.fromState(state);
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
	public IStateMatcher displayOnlyMatcher(IBlockState state) {
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
