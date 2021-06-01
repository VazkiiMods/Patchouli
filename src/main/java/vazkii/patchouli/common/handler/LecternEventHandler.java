package vazkii.patchouli.common.handler;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class LecternEventHandler {

	public static ActionResult rightClick(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		BlockPos pos = hit.getBlockPos();
		BlockState state = world.getBlockState(pos);
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof LecternBlockEntity) {
			if (state.get(LecternBlock.HAS_BOOK)) {
				if (player.isSneaking()) {
					takeBook(player, (LecternBlockEntity) tileEntity);
				} else {
					Book book = ItemStackUtil.getBookFromStack(((LecternBlockEntity) tileEntity).getBook());
					if (book != null) {
						if (!world.isClient) {
							PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
						}
						return ActionResult.SUCCESS;
					}
				}
			} else {
				ItemStack stack = player.getStackInHand(hand);
				if (ItemStackUtil.getBookFromStack(stack) != null) {
					if (LecternBlock.putBookIfAbsent(player, world, pos, state, stack)) {
						return ActionResult.SUCCESS;
					}
				}
			}
		}
		return ActionResult.PASS;
	}

	private static void takeBook(PlayerEntity player, LecternBlockEntity tileEntity) {
		ItemStack itemstack = tileEntity.getBook();
		tileEntity.setBook(ItemStack.EMPTY);
		LecternBlock.setHasBook(tileEntity.getWorld(), tileEntity.getPos(), tileEntity.getCachedState(), false);
		if (!player.getInventory().insertStack(itemstack)) {
			player.dropItem(itemstack, false);
		}
	}
}
