package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonBookBookmark extends GuiButtonBook {

	private final Book book;
	
	public final Bookmark bookmark;
	public final boolean multiblock;

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark) {
		this(parent, x, y, bookmark, false);
	}

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark, boolean multiblock) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, getTooltip(parent.book, bookmark, multiblock));
		this.book = parent.book;
		this.bookmark = bookmark;
		this.multiblock = multiblock;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if(visible && bookmark != null && entry != null) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			RenderHelper.enableGUIStandardItemLighting();
			int px = x * 2 + (hovered ? 6 : 2);
			int py = y * 2 + 2;
			entry.getIcon().render(px, py);

			GlStateManager.disableDepth();
			String s = Integer.toString(bookmark.page + 1);
			if(multiblock)
				s = I18n.format("patchouli.gui.lexicon.visualize_letter");
			mc.fontRenderer.drawStringWithShadow(s, px + 12, py + 10, 0xFFFFFF);
			GlStateManager.popMatrix();
		}
	}

	private static String[] getTooltip(Book book, Bookmark bookmark, boolean multiblock) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if(bookmark == null || entry == null)
			return new String[] { I18n.format("patchouli.gui.lexicon.add_bookmark") };

		return new String[] {
				entry.getName(),
				TextFormatting.GRAY + I18n.format(multiblock 
						? "patchouli.gui.lexicon.multiblock_bookmark"
								: "patchouli.gui.lexicon.remove_bookmark")
		};
	}

}
