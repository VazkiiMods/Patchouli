package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nullable;

import java.util.Collection;

@EventBusSubscriber(Dist.CLIENT)
public class BookRightClickHandler {

	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;
		ItemStack bookStack = player.getHeldItemMainhand();
		if (event.getType() == ElementType.ALL && mc.currentScreen == null) {
			Book book = getBookFromStack(bookStack);

			if (book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if (hover != null) {
					BookEntry entry = hover.getFirst();
					if (!entry.isLocked()) {
						MainWindow window = event.getWindow();
						int x = window.getScaledWidth() / 2 + 3;
						int y = window.getScaledHeight() / 2 + 3;
						entry.getIcon().render(x, y);
						RenderSystem.scalef(0.5F, 0.5F, 1F);
						mc.getItemRenderer().renderItemAndEffectIntoGUI(bookStack, (x + 8) * 2, (y + 8) * 2);
						RenderSystem.scalef(2F, 2F, 1F);

						mc.fontRenderer.drawStringWithShadow(entry.getName(), x + 18, y + 3, 0xFFFFFF);

						RenderSystem.pushMatrix();
						RenderSystem.scalef(0.75F, 0.75F, 1F);
						String s = I18n.format("patchouli.gui.lexicon." + (player.isSneaking() ? "view" : "sneak"));
						mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + s, (x + 18) / 0.75F, (y + 14) / 0.75F, 0xBBBBBB);
						RenderSystem.popMatrix();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRightClick(RightClickBlock event) {
		PlayerEntity player = event.getPlayer();

		if (event.getWorld().isRemote && player.isSneaking()) {
			Book book = getBookFromStack(event.getItemStack());

			if (book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if (hover != null) {
					int page = hover.getSecond() * 2;
					book.contents.setTopEntry(hover.getFirst().getId(), page);
				}
			}
		}
	}

	@Nullable
	public static Book getBookFromStack(ItemStack stack) {
		if (stack.getItem() instanceof ItemModBook) {
			return ItemModBook.getBook(stack);
		}

		Collection<Book> books = BookRegistry.INSTANCE.books.values();
		for (Book b : books) {
			if (b.getBookItem().isItemEqual(stack)) {
				return b;
			}
		}

		return null;
	}

	private static Pair<BookEntry, Integer> getHoveredEntry(Book book) {
		Minecraft mc = Minecraft.getInstance();
		RayTraceResult res = mc.objectMouseOver;
		if (res instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) res).getPos();
			BlockState state = mc.world.getBlockState(pos);
			Block block = state.getBlock();
			ItemStack picked = block.getPickBlock(state, res, mc.world, pos, mc.player);

			if (!picked.isEmpty()) {
				return book.contents.getEntryForStack(picked);
			}
		}

		return null;
	}

}
