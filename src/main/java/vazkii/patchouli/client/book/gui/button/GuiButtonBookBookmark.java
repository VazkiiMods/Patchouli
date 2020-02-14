package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;
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
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, parent::handleButtonBookmark, getTooltip(parent.book, bookmark, multiblock));
		this.book = parent.book;
		this.bookmark = bookmark;
		this.multiblock = multiblock;
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if(bookmark != null && entry != null) {
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			int px = x * 2 + (isHovered() ? 6 : 2);
			int py = y * 2 + 2;
			entry.getIcon().render(px, py);

			RenderSystem.disableDepthTest();
			String s = Integer.toString(bookmark.page + 1);
			if(multiblock)
				s = I18n.translate("patchouli.gui.lexicon.visualize_letter");
			parent.getMinecraft().textRenderer.drawWithShadow(s, px + 12, py + 10, 0xFFFFFF);
			RenderSystem.enableDepthTest();
			RenderSystem.popMatrix();
		}
	}

	private static String[] getTooltip(Book book, Bookmark bookmark, boolean multiblock) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if(bookmark == null || entry == null)
			return new String[] { I18n.translate("patchouli.gui.lexicon.add_bookmark") };

		return new String[] {
				entry.getName(),
				TextFormat.GRAY + I18n.translate(multiblock
						? "patchouli.gui.lexicon.multiblock_bookmark"
								: "patchouli.gui.lexicon.remove_bookmark")
		};
	}

}
