package vazkii.patchouli.client.handler;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookRightClickHandler {

	@SubscribeEvent
	public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		ItemStack bookStack = player.getHeldItemMainhand();
		if(event.getType() == ElementType.ALL && mc.currentScreen == null) { 
			Book book = getBookFromStack(bookStack);

			if(book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if(hover != null) {
					BookEntry entry = hover.getLeft();
					if(!entry.isLocked()) {
						ScaledResolution res = event.getResolution();
						int x = res.getScaledWidth() / 2 + 3;
						int y = res.getScaledHeight() / 2 + 3;
						entry.getIcon().render(x, y);
						GlStateManager.scale(0.5F, 0.5F, 1F);
						mc.getRenderItem().renderItemAndEffectIntoGUI(bookStack, (x + 8) * 2, (y + 8) * 2);
						GlStateManager.scale(2F, 2F, 1F);

						mc.fontRenderer.drawStringWithShadow(entry.getName(), x + 18, y + 3, 0xFFFFFF);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRightClick(RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack bookStack = player.getHeldItemMainhand();

		if(event.getWorld().isRemote && player.isSneaking()) {
			Book book = getBookFromStack(bookStack);

			if(book != null) {
				Pair<BookEntry, Integer> hover = getHoveredEntry(book);
				if(hover != null) {
					BookEntry entry = hover.getLeft();

					if(!entry.isLocked()) {
						int page = hover.getRight();
						GuiBook curr = book.contents.getCurrentGui();
						book.contents.currentGui = new GuiBookEntry(book, entry, page);

						if(curr instanceof GuiBookEntry) {
							GuiBookEntry currEntry = (GuiBookEntry) curr;
							if(currEntry.getEntry() == entry && currEntry.getPage() == page)
								return;
						}

						book.contents.guiStack.push(curr);
					}
				}
			}
		}
	}

	private static Book getBookFromStack(ItemStack stack) {
		if(stack.getItem() instanceof ItemModBook)
			return ItemModBook.getBook(stack);
		
		Collection<Book> books = BookRegistry.INSTANCE.books.values();
		for(Book b : books)
			if(b.getBookItem().isItemEqual(stack))
				return b;

		return null;
	}

	private static Pair<BookEntry, Integer> getHoveredEntry(Book book) {
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult res = mc.objectMouseOver;
		if(res.typeOfHit == Type.BLOCK) {
			BlockPos pos = res.getBlockPos();
			IBlockState state = mc.world.getBlockState(pos);
			Block block = state.getBlock();
			ItemStack picked = block.getPickBlock(state, res, mc.world, pos, mc.player);

			if(!picked.isEmpty()) 
				return book.contents.recipeMappings.get(ItemStackUtil.wrapStack(picked));
		}

		return null;
	}

}
