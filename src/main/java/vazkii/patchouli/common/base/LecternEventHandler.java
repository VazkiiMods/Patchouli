package vazkii.patchouli.common.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

/**
 * @author  Minecraftschurli
 * @version 2020-05-01
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LecternEventHandler {

	@SubscribeEvent
	static void rightClick(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof LecternTileEntity) {
				PlayerEntity player = event.getPlayer();
				if (state.get(LecternBlock.HAS_BOOK)) {
					if (player.isSneaking()) {
						takeBook(player, (LecternTileEntity) tileEntity);
					} else {
						if (((LecternTileEntity) tileEntity).getBook().getItem() instanceof ItemModBook) {
							Book book = ItemModBook.getBook(((LecternTileEntity) tileEntity).getBook());
							if (book != null) {
								PatchouliAPI.instance.openBookGUI((ServerPlayerEntity) player, book.id);
								event.setUseBlock(Event.Result.DENY);
							}
						}
					}
				} else {
					ItemStack stack = event.getItemStack();
					if (stack.getItem() instanceof ItemModBook) {
						if (LecternBlock.tryPlaceBook(world, pos, state, stack)) {
							event.setUseItem(Event.Result.ALLOW);
						}
					}
				}
			}
		}
	}

	private static void takeBook(PlayerEntity player, LecternTileEntity tileEntity) {
		ItemStack itemstack = tileEntity.field_214048_a.removeStackFromSlot(0);
		tileEntity.field_214048_a.markDirty();
		if (!player.inventory.addItemStackToInventory(itemstack)) {
			player.dropItem(itemstack, false);
		}
	}
}
