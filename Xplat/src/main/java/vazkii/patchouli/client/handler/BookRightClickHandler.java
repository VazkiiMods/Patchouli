package vazkii.patchouli.client.handler;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

import org.jetbrains.annotations.Nullable;

public class BookRightClickHandler {
	public static InteractionResult onRightClick(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
		ItemStack bookStack = player.getMainHandItem();

		if (world.isClientSide() && player.isShiftKeyDown()) {
			Book book = ItemStackUtil.getBookFromStack(bookStack);

			if (book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if (hover != null) {
					int page = hover.getSecond() * 2;
					book.getContents().setTopEntry(hover.getFirst().getId(), page);
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Nullable
	public static Pair<BookEntry, Integer> getHoveredEntry(Book book) {
		Minecraft mc = Minecraft.getInstance();
		HitResult res = mc.hitResult;
		if (mc.level != null && res instanceof BlockHitResult hit) {
			BlockPos pos = hit.getBlockPos();
			BlockState state = mc.level.getBlockState(pos);
			ItemStack picked = state.getCloneItemStack(mc.level, pos, false);

			if (!picked.isEmpty()) {
				return book.getContents().getEntryForStack(picked);
			}
		}

		return null;
	}
}
