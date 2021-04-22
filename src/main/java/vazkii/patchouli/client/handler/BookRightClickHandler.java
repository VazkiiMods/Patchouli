package vazkii.patchouli.client.handler;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookRightClickHandler {

	private static void onRenderHUD(MatrixStack ms, float partialTicks) {
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerEntity player = mc.player;
		ItemStack bookStack = player.getMainHandStack();
		if (mc.currentScreen == null) {
			Book book = ItemStackUtil.getBookFromStack(bookStack);

			if (book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if (hover != null) {
					BookEntry entry = hover.getFirst();
					if (!entry.isLocked()) {
						Window window = mc.getWindow();
						int x = window.getScaledWidth() / 2 + 3;
						int y = window.getScaledHeight() / 2 + 3;
						entry.getIcon().render(ms, x, y);
						ms.scale(0.5F, 0.5F, 1);
						RenderHelper.renderItemStackInGui(ms, bookStack, (x + 8) * 2, (y + 8) * 2);
						ms.scale(2F, 2F, 1F);

						mc.textRenderer.draw(ms, entry.getName(), x + 18, y + 3, 0xFFFFFF);

						ms.push();
						ms.scale(0.75F, 0.75F, 1F);
						Text s = new TranslatableText("patchouli.gui.lexicon." + (player.isSneaking() ? "view" : "sneak"))
								.formatted(Formatting.ITALIC);
						mc.textRenderer.draw(ms, s, (x + 18) / 0.75F, (y + 14) / 0.75F, 0xBBBBBB);
						ms.pop();
					}
				}
			}
		}
	}

	public static void init() {
		HudRenderCallback.EVENT.register(BookRightClickHandler::onRenderHUD);
		UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
	}

	private static ActionResult onRightClick(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		ItemStack bookStack = player.getMainHandStack();

		if (world.isClient && player.isSneaking()) {
			Book book = ItemStackUtil.getBookFromStack(bookStack);

			if (book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if (hover != null) {
					int page = hover.getSecond() * 2;
					book.contents.setTopEntry(hover.getFirst().getId(), page);
				}
			}
		}
		return ActionResult.PASS;
	}

	private static Pair<BookEntry, Integer> getHoveredEntry(Book book) {
		MinecraftClient mc = MinecraftClient.getInstance();
		HitResult res = mc.crosshairTarget;
		if (res instanceof BlockHitResult) {
			BlockPos pos = ((BlockHitResult) res).getBlockPos();
			BlockState state = mc.world.getBlockState(pos);
			Block block = state.getBlock();
			ItemStack picked = block.getPickStack(mc.world, pos, state);

			if (!picked.isEmpty()) {
				return book.contents.getEntryForStack(picked);
			}
		}

		return null;
	}

}
